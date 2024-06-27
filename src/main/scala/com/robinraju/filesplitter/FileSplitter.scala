package com.robinraju.filesplitter

trait FileSplitter {

  def splitFile(inputPath: String, outputPath: String, maxLines: Int, maxBytes: Int): Either[String, List[String]]

}
