package com.robinraju.filesplitter.application

import scopt.{ DefaultOParserSetup, OParser }

import com.robinraju.filesplitter.build.BuildInfo

object Cli {

  case class Config(
      inputFile: String,
      outputDir: String,
      maxLines: Int,
      maxBytes: Int
  )

  private val builder = OParser.builder[Config]
  private val parser = {
    import builder.*

    OParser.sequence(
      programName(BuildInfo.name),
      head("file-splitter", BuildInfo.version),
      opt[String]("input-file").action((x, c) => c.copy(inputFile = x)),
      opt[String]("output-dir")
        .required()
        .action((x, c) => c.copy(outputDir = x)),
      opt[Int]("max-lines")
        .required()
        .action((x, c) => c.copy(maxLines = x)),
      opt[Int]("max-bytes")
        .required()
        .action((x, c) => c.copy(maxBytes = x)),
      help("help").text("prints this usage text")
    )
  }

  private val setup = new DefaultOParserSetup {
    override def showUsageOnError: Option[Boolean] = Some(true)
  }

  def parseArgs(args: Array[String]): Option[Config] =
    OParser.parse(parser, args, Config(inputFile = "", outputDir = "", maxLines = -1, maxBytes = -1), setup)
}
