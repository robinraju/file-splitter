package com.robinraju.filesplitter.util

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class UtilsSpec extends AnyWordSpec with Matchers {

  "stripFileExtension" should {
    "return the file name without its extension" in {

      stripFileExtension("test.csv") shouldBe "test"
      stripFileExtension("test.2.csv") shouldBe "test.2"
      stripFileExtension("test.csv") shouldBe "test"
      stripFileExtension("test.") shouldBe "test"
      stripFileExtension(".") shouldBe ""
    }
  }

}
