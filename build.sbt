name := """finatra-seed"""
organization := "com.example"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.11.6"

fork in run := true

javaOptions ++= Seq(
  "-Dlog.service.output=/dev/stderr",
   "-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=4000,server=y,suspend=n",
   "-javaagent:/home/yo/Downloads/jrebel/jrebel.jar","-Drebel.log=true",
  "-Dlog.access.output=/dev/stderr")

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  "Twitter Maven" at "http://maven.twttr.com",
  "Finatra Repo" at "http://twitter.github.com/finatra"
)

libraryDependencies += "org.apache.oltu.oauth2" % "org.apache.oltu.oauth2.client" % "latest.milestone"

libraryDependencies += "org.apache.httpcomponents" % "httpclient" % "4.5"

libraryDependencies += "org.scalactic" % "scalactic_2.11" % "2.2.4"

libraryDependencies ++= Seq(

  "com.twitter.finatra" %% "finatra-http" % "2.0.0.M2",
  "com.twitter.finatra" %% "finatra-logback" % "2.0.0.M2",
  "com.twitter.finatra" %% "finatra-http" % "2.0.0.M2" % "test",
  "com.twitter.inject" %% "inject-server" % "2.0.0.M2" % "test",
  "com.twitter.inject" %% "inject-app" % "2.0.0.M2" % "test",
  "com.twitter.inject" %% "inject-core" % "2.0.0.M2" % "test",
  "com.twitter.inject" %% "inject-modules" % "2.0.0.M2" % "test",
  "com.twitter.finatra" %% "finatra-http" % "2.0.0.M2" % "test" classifier "tests",
  "com.twitter.inject" %% "inject-server" % "2.0.0.M2" % "test" classifier "tests",
  "com.twitter.inject" %% "inject-app" % "2.0.0.M2" % "test" classifier "tests",
  "com.twitter.inject" %% "inject-core" % "2.0.0.M2" % "test" classifier "tests",
  "com.twitter.inject" %% "inject-modules" % "2.0.0.M2" % "test" classifier "tests",

  "org.mockito" % "mockito-core" % "1.9.5" % "test",
  "org.scalatest" %% "scalatest" % "2.2.3" % "test",
  "org.specs2" %% "specs2" % "2.3.12" % "test")
