val Http4sVersion = "0.21.4"
val CirceVersion = "0.13.0"
val Specs2Version = "4.9.3"
val LogbackVersion = "1.2.3"

val AkkaVersion = "2.6.8"
val AkkaHttpVersion = "10.2.0"

lazy val root = (project in file("."))
  .settings(
    organization := "app.k8ty",
    name := "k8ty-api",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.13.2",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
      "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
      "org.http4s"      %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s"      %% "http4s-blaze-client" % Http4sVersion,
      "org.http4s"      %% "http4s-circe"        % Http4sVersion,
      "org.http4s"      %% "http4s-dsl"          % Http4sVersion,
      "io.circe"        %% "circe-core"       % CirceVersion,
      "io.circe"        %% "circe-generic"       % CirceVersion,
      "io.circe"        %% "circe-parser"       % CirceVersion,
      "org.specs2"      %% "specs2-core"         % Specs2Version % "test",
      "ch.qos.logback"  %  "logback-classic"     % LogbackVersion,
      "dev.zio"         %% "zio"                 % "1.0.0",
      "dev.zio"         %% "zio-streams"                 % "1.0.0",
      "dev.zio"         %% "zio-logging"         % "0.4.0",
      "dev.zio"         %% "zio-interop-cats"    % "2.1.4.0",
      "com.github.pureconfig" %% "pureconfig" % "0.13.0",
      "org.tpolecat" %% "doobie-core" % "0.9.0",
      "org.tpolecat" %% "doobie-postgres" % "0.9.0",
      "org.tpolecat" %% "doobie-quill" % "0.9.0",
      "io.getquill" %% "quill-jdbc" % "3.5.2",
      "org.postgresql" % "postgresql" % "42.2.14",
      "com.google.firebase" % "firebase-admin" % "6.14.0",
      "com.github.ghostdogpr"        %% "caliban" % "0.9.0",
      "com.github.ghostdogpr"        %% "caliban-http4s" % "0.9.0",
      "com.github.ghostdogpr"        %% "caliban-akka-http" % "0.9.1",
      "de.heikoseeberger"            %% "akka-http-circe" % "1.32.0",
    ),
    addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.10.3"),
    addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1")
  ).enablePlugins(DockerPlugin, JavaServerAppPackaging)

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-language:higherKinds",
  "-language:postfixOps",
  "-feature",
  "-Xfatal-warnings",
)

//  https://devcenter.heroku.com/articles/java-support#environment
javaOptions in Universal ++= Seq(
  "-Xmx300m",
  "-Xss512k",
  "-XX:CICompilerCount=2"
)

dockerExposedPorts  ++= Seq(9000)
dockerBaseImage := "adoptopenjdk/openjdk14-openj9"
