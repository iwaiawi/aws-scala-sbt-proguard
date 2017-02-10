import sbt.Keys._
import sbt._
import sbtrelease.Version

name := "hello"

resolvers += Resolver.sonatypeRepo("public")
scalaVersion := "2.12.1"
releaseNextVersion := { ver => Version(ver).map(_.bumpMinor.string).getOrElse("Error") }

libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-lambda-java-events" % "1.3.0",
  "com.amazonaws" % "aws-lambda-java-core" % "1.1.0"
)

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-feature",
  "-Xfatal-warnings")

proguardSettings

ProguardKeys.proguardVersion in Proguard := "5.3.2"

ProguardKeys.inputs in Proguard := Seq(baseDirectory.value / "target" / s"scala-${scalaVersion.value.dropRight(2)}" / s"${name.value}-assembly-${version.value}.jar")

ProguardKeys.outputs in Proguard := Seq(baseDirectory.value / "target" / s"scala-${scalaVersion.value.dropRight(2)}" / s"${name.value}-proguard.jar")

//ProguardKeys.inputFilter in Proguard := { file => None }

ProguardKeys.merge in Proguard := false

(ProguardKeys.proguard in Proguard) <<= (ProguardKeys.proguard in Proguard).dependsOn(assembly)

ProguardKeys.options in Proguard ++= Seq(
  "-dontobfuscate",
  "-ignorewarnings",

  // Main
  "-keep class hello.** { *; }",

  // Java
  "-keepattributes InnerClasses",
  "-keepattributes Signature",
  "-keepattributes InnerClasses,EnclosingMethod",
  "-keepattributes *Annotation*",

  // Scala
  "-keep class scala.collection.SeqLike { public protected *; }",
  "-keep class scala.collection.immutable.StringLike { *; }",
  "-keep class scala.concurrent.**",
  "-keep class scala.beans.BeanProperty",
  "-keep class scala.reflect.ScalaSignature.**",
  "-keep class scala.Dynamic",
  "-keep class * extends java.util.ListResourceBundle { protected java.lang.Object[][] getContents(); }",
  "-dontwarn scala.collection.**", // required from Scala 2.11.4
  "-dontnote scala.**",

  // AWS
  "-keep class com.amazonaws.**", // Class names are needed in reflection
  "-dontwarn com.amazonaws.**",
  "-dontnote com.amazonaws.**",

  // slf4j
  "-dontwarn org.slf4j.**",

  // apache-httpcomponent
  "-dontwarn org.apache.http.**",
  "-dontnote org.apache.http.**",

  // joda-time
  "-dontwarn org.joda.convert.FromString",
  "-dontwarn org.joda.convert.ToString",
  "-dontnote org.joda.time.DateTimeZone",

  // commons-logging
  "-dontwarn org.apache.commons.logging.**",
  "-dontnote org.apache.commons.logging.LogSource",
  "-dontnote org.apache.commons.logging.impl.Log4JLogger",

  // jackson
  "-dontwarn com.fasterxml.jackson.**"
)
