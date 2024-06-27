package com.robinraju.filesplitter.splitter

import java.nio.channels.FileChannel
import java.nio.charset.StandardCharsets
import java.nio.file.{ Files, Path, Paths, StandardOpenOption }
import java.nio.{ ByteBuffer, MappedByteBuffer }

import scala.util.{ Failure, Success, Try }

import com.robinraju.filesplitter.FileSplitter
import com.robinraju.filesplitter.util.stripFileExtension

class CSVFileSplitter extends FileSplitter {

  override def splitFile(
      inputPath: String,
      outputPath: String,
      maxLines: Int,
      maxBytes: Int
  ): Either[String, List[String]] =
    Try(FileChannel.open(Path.of(inputPath), StandardOpenOption.READ)) match {
      case Failure(exception) => Left(s"Failed to open file at $inputPath, error: ${exception.getMessage}")
      case Success(fileChannel) =>
        val fileName                       = Paths.get(inputPath).getFileName.toString
        val fileSize                       = fileChannel.size()
        val mappedBuffer: MappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileSize)

        val (header, nextPos) = readLine(mappedBuffer, 0)
        val headerSize        = header.getBytes(StandardCharsets.UTF_8).length + 1

        @scala.annotation.tailrec
        def processLines(
            currentPos: Int,
            lineCount: Int,
            byteCount: Int,
            partNumber: Int,
            partChannel: FileChannel,
            partFiles: List[String]
        ): List[String] =
          if (currentPos >= fileSize) {
            // All lines processed in the current part file.
            partChannel.close()
            partFiles.reverse
          } else {
            val (line, newPos) = readLine(mappedBuffer, currentPos)
            val lineSize       = line.getBytes(StandardCharsets.UTF_8).length + 1

            (lineCount >= maxLines, byteCount + lineSize > maxBytes) match {
              case (true, _) | (_, true) =>
                partChannel.close()
                // Create a new part file
                val newPartNumber               = partNumber + 1
                val (partFileName, newPartFile) = createNewPartFile(outputPath, fileName, newPartNumber, header)
                writeLineToPartFile(newPartFile, line)
                processLines(newPos, 1, headerSize + lineSize, newPartNumber, newPartFile, partFileName :: partFiles)
              case _ =>
                writeLineToPartFile(partChannel, line)
                processLines(newPos, lineCount + 1, byteCount + lineSize, partNumber, partChannel, partFiles)
            }
          }

        val (partFileName, initialPartFile) = createNewPartFile(outputPath, fileName, 0, header)
        // Start processing lines
        val processedFiles = processLines(nextPos, 1, headerSize, 0, initialPartFile, List(partFileName))

        fileChannel.close()
        Right(processedFiles)
    }

  @scala.annotation.tailrec
  private def readLine(
      buffer: MappedByteBuffer,
      currentPos: Int,
      acc: StringBuilder = new StringBuilder
  ): (String, Int) =
    if (currentPos >= buffer.limit()) {
      (acc.toString(), currentPos)
    } else {
      buffer.get(currentPos).toChar match {
        case '\n' => (acc.toString(), currentPos + 1)
        case char => readLine(buffer, currentPos + 1, acc.append(char))
      }
    }

  private def createNewPartFile(
      outputPath: String,
      fileName: String,
      partNumber: Int,
      header: String
  ): (String, FileChannel) = {
    val partFileName = s"$outputPath/${stripFileExtension(fileName)}-part$partNumber.csv"
    val partPath     = Paths.get(partFileName)

    // Ensure the output directory exists
    val outputDir = partPath.getParent
    if (!Files.exists(outputDir)) {
      Files.createDirectories(outputDir)
      println(s"Created directory: $outputDir")
    }

    val partFile = FileChannel.open(partPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE)
    // Write the header to the new part file
    partFile.write(ByteBuffer.wrap((header + "\n").getBytes(StandardCharsets.UTF_8)))
    println(s"Created part file $partFileName")
    (partFileName, partFile)
  }

  private def writeLineToPartFile(fileChannel: FileChannel, line: String): Int =
    fileChannel.write(ByteBuffer.wrap((line + "\n").getBytes(StandardCharsets.UTF_8)))
}
