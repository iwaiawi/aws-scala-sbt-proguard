package hello

import java.io.{InputStream, OutputStream}
import java.nio.charset.StandardCharsets.UTF_8
import com.amazonaws.services.lambda.runtime.Context
import spray.json._
import scala.io.Source

abstract class ScalaHandler[I, O](implicit jsonReader: JsonReader[I], jsonWriter: JsonWriter[O]) {

  // This method should be overriden
  def handle(input: I, context: Context): O

  def handleThrowable(e: Throwable, os: OutputStream, context: Context): Unit = {
    throw e
  }

  // This function will ultimately be used as the external handler
  def handle(is: InputStream, os: OutputStream, context: Context): Unit = {
    try {
      val input = Source.fromInputStream(is).mkString.parseJson.convertTo[I]
      os.write(handle(input, context).toJson.prettyPrint.getBytes(UTF_8))
    } catch {
      case e: Throwable => handleThrowable(e, os, context)
    } finally {
      os.close()
    }
  }
}
