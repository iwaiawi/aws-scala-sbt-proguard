import sbt.Keys._
import sbt._
import sbtrelease.Version

name := "hello"

resolvers += Resolver.sonatypeRepo("public")
scalaVersion := "2.11.11"
releaseNextVersion := { ver => Version(ver).map(_.bumpMinor.string).getOrElse("Error") }

libraryDependencies ++= Seq(
  "com.github.fommil" %% "spray-json-shapeless" % "1.4.0",
  "io.symphonia" % "lambda-logging" % "1.0.0",
  "com.amazonaws" % "aws-lambda-java-events" % "1.3.0",
  "com.amazonaws" % "aws-lambda-java-core" % "1.1.0"
)

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-feature",
  "-Xfatal-warnings"
)

proguardSettings

ProguardKeys.proguardVersion in Proguard := "5.3.3"

ProguardKeys.inputs in Proguard := Seq(baseDirectory.value / "target" / s"scala-${scalaVersion.value.dropRight(2)}" / s"${name.value}-assembly-${version.value}.jar")

ProguardKeys.outputs in Proguard := Seq(baseDirectory.value / "target" / s"scala-${scalaVersion.value.dropRight(2)}" / s"${name.value}-proguard.jar")

ProguardKeys.merge in Proguard := false

(ProguardKeys.proguard in Proguard) <<= (ProguardKeys.proguard in Proguard).dependsOn(assembly)

javaOptions in (Proguard, ProguardKeys.proguard) := Seq("-Xmx2048M", "-Xms512M", "-XX:MaxMetaspaceSize=2048M")

ProguardKeys.options in Proguard ++= Seq(
  // Basic
  "-dontobfuscate",
  "-ignorewarnings",
  "-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*,!code/allocation/variable",

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
  "-keep public class scala.reflect.ScalaSignature",
  "-keep class scala.reflect.ScalaSignature.**",
  "-keep class scala.Dynamic { *; }",
  "-keep public interface scala.ScalaObject", // This is gone in 2.11
  "-keepclassmembers class * { ** MODULE$;}",
  "-keepclassmembernames class scala.concurrent.forkjoin.ForkJoinTask { int status; }",

  """|-keepclassmembernames class scala.concurrent.forkjoin.ForkJoinPool {
     |  long eventCount;
     |  int workerCounts;
     |  int runControl;
     |  scala.concurrent.forkjoin.ForkJoinPool$WaitQueueNode syncStack;
     |  scala.concurrent.forkjoin.ForkJoinPool$WaitQueueNode spareStack;
     |}""".stripMargin,

  """|-keepclassmembernames class scala.concurrent.forkjoin.ForkJoinWorkerThread {
     |  int base;
     |  int sp;
     |  int runState;
     |}""".stripMargin,

  """|-keepclassmembernames class scala.concurrent.forkjoin.LinkedTransferQueue {
     |  scala.concurrent.forkjoin.LinkedTransferQueue$PaddedAtomicReference head;
     |  scala.concurrent.forkjoin.LinkedTransferQueue$PaddedAtomicReference tail;
     |  scala.concurrent.forkjoin.LinkedTransferQueue$PaddedAtomicReference cleanMe;
     |}""".stripMargin,

  "-keep class * extends java.util.ListResourceBundle { protected java.lang.Object[][] getContents(); }",
  "-dontnote scala.**",
  "-dontnote org.xml.sax.EntityResolver",
  "-dontwarn scala.collection.**", // required from Scala 2.11.4
  "-dontwarn scala.beans.ScalaBeanInfo",
  "-dontwarn scala.concurrent.**",
  "-dontwarn scala.reflect.**",
  "-dontwarn scala.sys.process.package$",
  "-dontwarn **$$anonfun$*",
  "-dontwarn scala.collection.immutable.RedBlack$Empty",
  "-dontwarn scala.tools.**,plugintemplate.**",

  // AWS
  "-keep class com.amazonaws.**", // Class names are needed in reflection
  "-keep class com.amazonaws.auth.** { *; }", // Class names are needed in reflection
  "-dontwarn com.amazonaws.**",
  "-dontnote com.amazonaws.**",

  // slf4j
  "-keep class org.slf4j.** { *; }",
  "-keep class ch.qos.logback.** { *; }",
  "-keep class org.apache.log4j.** { *; }",
  "-dontwarn org.slf4j.**",
  "-dontwarn ch.qos.logback.**",
  "-dontnote org.slf4j.**",
  "-dontnote ch.qos.logback.**",

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

  // spray-json
  "-keep class spray.json.**",
  "-dontnote spray.json.**",

  // spray-json-shapeless
  "-keep class fommil.sjs.**",
  "-dontwarn shapeless.**",
  "-dontnote fommil.sjs.**",
  "-dontnote shapeless.**",

  // jackson
  "-dontwarn com.fasterxml.jackson.**"
)
