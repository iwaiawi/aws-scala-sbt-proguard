package hello

import com.amazonaws.services.lambda.runtime.Context
import fommil.sjs.FamilyFormats._

class Handler extends ScalaHandler[Request, Response] {

  override def handle(request: Request, context: Context): Response = {
    Response("Go Serverless v1.0! Your function executed successfully!", request)
  }
}


