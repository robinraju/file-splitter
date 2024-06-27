// scalafmt -  Format source files
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.5.2")

// scalafix - Rules for code format: organize imports
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.12.1")

// Generate build info
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.12.0")

addSbtPlugin("com.github.sbt" % "sbt-native-packager" % "1.10.0")
