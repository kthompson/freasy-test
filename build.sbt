name := "freasy-test"

version := "1.0"

scalaVersion := "2.11.5"

resolvers += Resolver.jcenterRepo

libraryDependencies ++= Seq(
  "com.thangiee" %% "freasy-monad" % "0.4.0",
  "org.typelevel" %% "cats" % "0.7.2"
)
