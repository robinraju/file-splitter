package com.robinraju.filesplitter

import com.robinraju.filesplitter.application.Cli
import com.robinraju.filesplitter.splitter.CSVFileSplitter

object Main {

  private val fileSplitter: FileSplitter = new CSVFileSplitter()

  def main(args: Array[String]): Unit =
    Cli.parseArgs(args) match {
      case Some(config) =>
        fileSplitter
          .splitFile(config.inputFile, config.outputDir, config.maxLines, config.maxBytes)
          .fold(
            error => println(s"Failed to split files: $error"),
            partFiles => println(s"Completed: ${partFiles.size} part files created")
          )
      case None =>
        println("Failed to parse args")
    }

}
