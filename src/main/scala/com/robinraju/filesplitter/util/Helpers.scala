package com.robinraju.filesplitter.util

def stripFileExtension(str: String): String =
  str.lastIndexOf(".") match {
    case -1  => str
    case idx => str.substring(0, idx)
  }
