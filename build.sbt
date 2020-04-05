import sbt.addCompilerPlugin

name := "FrontEndLab"

version := "0.1"

scalaVersion := "2.13.1"

lazy val global = project
  .in(file("."))
  .settings(
    settings
  )
  .aggregate(
    model,
    rest_api,
    web
  )

lazy val model = project
  .settings(
    name := "model",
    settings,
    libraryDependencies ++= modelDeps
  )


lazy val repositories = project
  .settings(
    name := "repositories",
    settings,
    libraryDependencies ++= repoDeps
  )
  .dependsOn(model)

lazy val rest_api = project
  .settings(
    name := "rest_api",
    assemblySettings,
    settings,
    libraryDependencies ++= restApiDeps
  )
  .dependsOn(repositories)

lazy val web = project
  .enablePlugins(PlayScala)
  .settings(
    name := """web""",
    libraryDependencies ++= webDeps,
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-Xfatal-warnings"
    )
  )
  .dependsOn(repositories)


lazy val settings =
  commonSettings ++
    scalafmtSettings

lazy val dependencies =
  new {
    val logbackV = "1.2.3"
    val scalaLoggingV = "3.7.2"
    val typesafeConfigV = "1.3.1"
    val pureconfigV = "0.8.0"
    val monocleV = "1.4.0"
    val akkaV = "2.5.6"
    val akkaHttpV = "10.1.7"
    val scalatestV = "3.0.4"
    val scalacheckV = "1.13.5"
    val chimneyV = "0.5.0"
    val slickV = "3.3.2"
    val circeV = "0.12.3"
    val httpCirceV = "1.27.0"

    val httpCirce = "de.heikoseeberger" %% "akka-http-circe" % httpCirceV
    val logback = "ch.qos.logback" % "logback-classic" % logbackV
    val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingV
    val typesafeConfig = "com.typesafe" % "config" % typesafeConfigV
    val akka = "com.typesafe.akka" %% "akka-stream" % akkaV
    val akkaHttp = "com.typesafe.akka" %% "akka-http" % akkaHttpV
    val chimney = "io.scalaland" %% "chimney" % chimneyV
    val slick = "com.typesafe.slick" %% "slick-hikaricp" % slickV
    val scalatest = "org.scalatest" %% "scalatest" % scalatestV
    val scalacheck = "org.scalacheck" %% "scalacheck" % scalacheckV
    val circeDeps = Seq(
      "io.circe" %% "circe-core",
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-parser"
    ).map(_ % circeV)
    val h2 = "com.h2database" % "h2" % "1.4.199"
    val shapeless = "com.chuusai" %% "shapeless" % "2.3.3"
  }

lazy val commonDependencies = Seq(
  dependencies.logback,
  dependencies.scalaLogging,
  dependencies.typesafeConfig,
  dependencies.scalatest % "test",
  dependencies.scalacheck % "test"
)

lazy val modelDeps =
  commonDependencies ++
    dependencies.circeDeps :+
    dependencies.chimney :+
    compilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full) :+
    dependencies.shapeless


lazy val restApiDeps = commonDependencies ++
  Seq(dependencies.akka,
    dependencies.akkaHttp,
    dependencies.httpCirce) ++
  dependencies.circeDeps :+
  dependencies.h2

lazy val repoDeps = commonDependencies ++ Seq(
  dependencies.slick
)

lazy val webDeps = commonDependencies ++ Seq(
  guice,
  "com.typesafe.play" %% "play-slick" % "5.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "5.0.0",
  "org.webjars" % "jquery" % "3.4.1",
  "org.webjars" % "bootstrap" % "4.4.1-1",
  specs2 % Test,
) :+ dependencies.h2

lazy val compilerOptions = Seq(
  "-unchecked",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-deprecation",
  "-encoding",
  "utf8"
)

lazy val commonSettings = Seq(
  scalacOptions ++= compilerOptions,
  autoCompilerPlugins := true,
  resolvers ++= Seq(
    "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
  )
)

lazy val scalafmtSettings =
  Seq(
    scalafmtOnCompile := true,
    scalafmtTestOnCompile := true,
    scalafmtVersion := "1.2.0"
  )

lazy val assemblySettings = Seq(
  assemblyJarName in assembly := name.value + ".jar",
  assemblyMergeStrategy in assembly := {
    case PathList("META-INF", xs@_*) => MergeStrategy.discard
    case "application.conf" => MergeStrategy.concat
    case x =>
      val oldStrategy = (assemblyMergeStrategy in assembly).value
      oldStrategy(x)
  }
)
