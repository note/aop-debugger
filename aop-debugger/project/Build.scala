import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "aop-debugger"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    javaCore,
    javaJdbc,
    javaEbean,
    "org.aspectj" % "aspectjweaver" % "1.7.2",
    "org.aspectj" % "aspectjrt" % "1.7.2",
    "com.google.guava" % "guava" % "14.0.1"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )

}
