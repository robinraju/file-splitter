package com.robinraju.filesplitter

import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.{ FileVisitResult, Files, Path, Paths, SimpleFileVisitor }

import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import com.robinraju.filesplitter.splitter.CSVFileSplitter

class CSVFileSplitterSpec extends AnyWordSpecLike with Matchers with BeforeAndAfterEach {
  import CSVFileSplitterSpec._

  override def afterEach(): Unit = {
    deleteDirectory(outputPath)
    super.afterEach()
  }

  "CSVFileSplitter" should {
    "split a large csv file into parts by maxLines" in {
      val inputPath = "src/test/resources/currency.csv"

      fileSplitter.splitFile(inputPath, outputPath, maxLines = 2, maxBytes = 1000)

      val part0 = Paths.get(s"$outputPath/currency-part0.csv")
      val part1 = Paths.get(s"$outputPath/currency-part1.csv")
      val part2 = Paths.get(s"$outputPath/currency-part2.csv")
      val part3 = Paths.get(s"$outputPath/currency-part3.csv")
      val part4 = Paths.get(s"$outputPath/currency-part4.csv")

      List(part0, part1, part2, part3, part4).forall(path => Files.exists(path)) shouldBe true

    }

    "split a large csv file into parts by maxBytes" in {
      val inputPath   = "src/test/resources/currency.csv"
      val MaxFileSize = 100
      fileSplitter.splitFile(inputPath, outputPath, maxLines = 500, maxBytes = MaxFileSize)

      val part0 = Paths.get(s"$outputPath/currency-part0.csv")
      val part1 = Paths.get(s"$outputPath/currency-part1.csv")
      val part2 = Paths.get(s"$outputPath/currency-part2.csv")
      val part3 = Paths.get(s"$outputPath/currency-part3.csv")

      val part0Content = new String(Files.readAllBytes(part0), StandardCharsets.UTF_8)
      val part1Content = new String(Files.readAllBytes(part1), StandardCharsets.UTF_8)
      val part2Content = new String(Files.readAllBytes(part2), StandardCharsets.UTF_8)
      val part3Content = new String(Files.readAllBytes(part3), StandardCharsets.UTF_8)

      List(part0, part1, part2, part3).map(_.toFile.length()).forall(_ < MaxFileSize) shouldBe true

      """Code,Symbol,Name
        |AED,￘ﾯ.￘ﾥ,United Arab Emirates d
        |AFN,￘ﾋ,Afghan afghani
        |""".stripMargin shouldEqual part0Content

      """Code,Symbol,Name
        |ALL,L,Albanian lek
        |AMD,AMD,Armenian dram
        |ANG,ￆﾒ,Netherlands Antillean gu
        |""".stripMargin shouldEqual part1Content

      """Code,Symbol,Name
        |AOA,Kz,Angolan kwanza
        |ARS,$,Argentine peso
        |AUD,$,Australian dollar
        |""".stripMargin shouldEqual part2Content

      """Code,Symbol,Name
        |AWG,Afl.,Aruban florin
        |AZN,AZN,Azerbaijani manat
        |""".stripMargin shouldEqual part3Content

    }
  }

}

object CSVFileSplitterSpec {
  val outputPath   = "src/test/resources/output"
  val fileSplitter = new CSVFileSplitter()

  def deleteDirectory(dirPath: String): Path =
    Files.walkFileTree(
      Paths.get(dirPath),
      new SimpleFileVisitor[Path] {
        override def visitFile(
            file: Path,
            attrs: BasicFileAttributes
        ): FileVisitResult = {
          Files.delete(file)
          FileVisitResult.CONTINUE
        }
        override def postVisitDirectory(
            dir: Path,
            exc: IOException
        ): FileVisitResult = {
          Files.delete(dir)
          FileVisitResult.CONTINUE
        }
      }
    )
}
