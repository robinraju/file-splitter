// *****************************************************************************
// Build settings
// *****************************************************************************

inThisBuild(
  Seq(
    version           := "0.0.1",
    organization      := "com.robinraju",
    organizationName  := "Robin Raju",
    scalaVersion      := "3.4.2",
    semanticdbEnabled := true,
    scalacOptions ++= Seq(
      "-deprecation",
      "-encoding",
      "UTF-8",
      "-feature",
      "-unchecked",
      "-Wunused:implicits",
      "-Wunused:explicits",
      "-Wunused:imports",
      "-Wunused:locals",
      "-Wunused:params",
      "-Wunused:privates",
      "-Wvalue-discard",
      "-Ykind-projector:underscores",
      "-rewrite",
      "-source",
      "3.4-migration"
    ),
    scalafmtOnCompile := true,
    fork              := true
  )
)

// *****************************************************************************
// Projects
// *****************************************************************************

lazy val root = (project in file("."))
  .settings(
    name := "file-splitter",
    libraryDependencies ++= Dependencies.libraries,
    Compile / run / mainClass := Some("com.robinraju.filesplitter.Main")
  )
  .settings(buildInfoSettings("file-splitter"))
  .enablePlugins(BuildInfoPlugin, JavaAppPackaging)

// *****************************************************************************
// Project settings
// *****************************************************************************

def buildInfoSettings(moduleName: String): Def.SettingsDefinition = Seq(
  buildInfoKeys := Seq[BuildInfoKey](
    "name" -> moduleName,
    version,
    scalaVersion,
    sbtVersion,
    organizationName
  ),
  buildInfoPackage := s"com.robinraju.filesplitter.build",
  buildInfoOptions += BuildInfoOption.BuildTime
)

addCommandAlias(
  name = "fixall",
  value = "scalafmtAll ;scalafixAll ;reload"
)
