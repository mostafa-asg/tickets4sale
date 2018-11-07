name := "tickts4sale"
organization := "tickets4sale"
version := "0.1"
scalaVersion := "2.12.6"

resolvers += Classpaths.typesafeReleases
resolvers += Classpaths.sbtPluginReleases

lazy val core = project.settings(libraryDependencies ++= Dependencies.commonDependencies)

lazy val cli = project.dependsOn(core).
                       settings(libraryDependencies ++= Dependencies.commonDependencies).
                       settings(
                         mainClass in assembly := Some("tickets4sale.cli.Main"),
                         assemblyJarName in assembly := "tickets4sale-cli.jar"
                       )

lazy val root = (project in file(".")).aggregate(core,cli)