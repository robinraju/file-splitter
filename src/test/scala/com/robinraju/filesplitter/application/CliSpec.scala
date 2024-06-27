package com.robinraju.filesplitter.application

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import com.robinraju.filesplitter.application.Cli.Config

class CliSpec extends AnyWordSpecLike with Matchers {

  "Cli" should {
    "parse command line args" in {
      val parsed = Cli.parseArgs(
        Array("--input-file", "./input.csv", "--output-dir", "./output", "--max-lines", "1000", "--max-bytes", "500")
      )

      val config = Config(
        inputFile = "./input.csv",
        outputDir = "./output",
        maxLines = 1000,
        maxBytes = 500
      )

      parsed shouldBe Some(config)
    }

    "fail parsing invalid args" in {
      val parsed = Cli.parseArgs(
        Array("--input-file", "./input.csv", "--max-lines", "1000", "--max-bytes", "500")
      )

      parsed shouldBe None
    }
  }

}
