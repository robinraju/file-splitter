// *****************************************************************************
// Library dependencies
// *****************************************************************************

import sbt.*

object Dependencies {

  object Versions {
    val ScoptVersion     = "4.1.0"
    val ScalaTestVersion = "3.2.9"
  }

  val libraries: Seq[ModuleID] = Seq(
    "com.github.scopt" %% "scopt"     % Versions.ScoptVersion,
    "org.scalatest"    %% "scalatest" % Versions.ScalaTestVersion % Test
  )
}
