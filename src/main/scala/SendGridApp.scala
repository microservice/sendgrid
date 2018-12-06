import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server._
import akka.stream.ActorMaterializer
import StatusCodes._
import scala.io.StdIn
import scala.collection.JavaConverters._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._
import com.sendgrid._
import java.io.IOException

// SendGrid API objects

case class SendGridEmail(
  email: String,
  name: Option[String]
)

case class SendGridPersonalization(
  to: List[SendGridEmail],
  cc: Option[List[SendGridEmail]],
  bcc: Option[List[SendGridEmail]],
  subject: Option[String],
  headers: Option[Map[String,String]],
  substitutions: Option[Map[String,String]],
  dynamic_template_data: Option[Map[String,String]],
  custom_args: Option[Map[String,String]],
  send_at: Option[Int]
)

case class SendGridContents(
  _type: String,
  value: String
)

case class SendGridAttachment(
  content: String,
  _type: Option[String],
  filename: String,
  disposition: Option[String],
  content_id: Option[String]
)

case class SendGridAsm(
  group_id: Int,
  groups_to_display: Option[List[Int]]
)

case class SendGridMailSettingsBCC(
  enable: Option[Boolean],
  email: Option[String]
)

case class SendGridMailSettingsBypass(
  enable: Option[Boolean]
)

case class SendGridMailSettingsFooter(
  enable: Option[Boolean],
  text: Option[String],
  html: Option[String]
)

case class SendGridMailSettingsSandbox(
  enable: Option[Boolean]
)

case class SendGridMailSettingsSpamCheck(
  enable: Option[Boolean],
  threshold: Option[Int],
  post_to_url: Option[String]
)

case class SendGridMailSettings(
  bcc: Option[SendGridMailSettingsBCC],
  bypass_list_management: Option[SendGridMailSettingsBypass],
  footer: Option[SendGridMailSettingsFooter],
  sandbox_mode: Option[SendGridMailSettingsSandbox],
  spam_check: Option[SendGridMailSettingsSpamCheck]
)

case class SendGridClickTracking(
  enable: Option[Boolean],
  enable_text: Option[Boolean]
)

case class SendGridOpenTracking(
  enable: Option[Boolean],
  substitution_tag: Option[String]
)

case class SendGridSubscriptionTracking(
  enable: Option[Boolean],
  text: Option[String],
  html: Option[String],
  substitution_tag: Option[String]
)

case class SendGridGAnalyticsTracking(
  enable: Option[Boolean],
  utm_source: Option[String],
  utm_medium: Option[String],
  utm_term: Option[String],
  utm_content: Option[String],
  utm_campaign: Option[String]
)

case class SendGridTrackingSettings(
  click_traing: Option[SendGridClickTracking],
  open_tracking: Option[SendGridOpenTracking],
  subscription_tracking: Option[SendGridSubscriptionTracking],
  ganalytics_tracking: Option[SendGridGAnalyticsTracking]
)

case class SendGridRequest(
  personalizations: List[SendGridPersonalization],
  from: SendGridEmail,
  reply_to: Option[SendGridEmail],
  subject: Option[String],
  content: List[SendGridContents],
  attachments: Option[List[SendGridAttachment]],
  template_id: Option[String],
  sections: Option[Map[String,String]],
  headers: Option[Map[String,String]],
  categories: Option[List[String]],
  custom_args: Option[Map[String,String]],
  send_at: Option[Int],
  batch_id: Option[String],
  asm: Option[SendGridAsm],
  ip_pool_name: Option[String],
  mail_settings: Option[SendGridMailSettings],
  tracking_settings: Option[SendGridTrackingSettings]
)

case class SendGridError(
  message: String,
  field: Option[String],
  help: Option[String]
)

case class SendGridErrorResponse(
  errors: Option[List[SendGridError]]
)

// -- own request objects

case class EmailRequest(
  from: String,
  to: String,
  subject: String,
  content: String,
  content_type: Option[String] = Some("text/plain")
)

case class ErrorMessage(success: Boolean, message: String)
case class UserResponse(message_id: String)

// Teach SprayJson about all our objects
// https://github.com/spray/spray-json
trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val emailFormat = jsonFormat5(EmailRequest)
  implicit val errorFormat = jsonFormat2(ErrorMessage)
  implicit val userResponse = jsonFormat1(UserResponse)

  implicit val sendGridEmail = jsonFormat2(SendGridEmail)
  implicit val sendGridPersonalization = jsonFormat9(SendGridPersonalization)
  implicit val sendGridContents = jsonFormat(SendGridContents.apply, "type", "value")
  implicit val sendGridAttachment = jsonFormat(SendGridAttachment.apply, "content", "type", "filename", "disposition", "content_id")
  implicit val sendGridAsm = jsonFormat2(SendGridAsm)
  implicit val sendGridMailSettingsBCC = jsonFormat2(SendGridMailSettingsBCC)
  implicit val sendGridMailSettingsBypass = jsonFormat1(SendGridMailSettingsBypass)
  implicit val sendGridMailSettingsFooter = jsonFormat3(SendGridMailSettingsFooter)
  implicit val sendGridMailSettingsSandbox = jsonFormat1(SendGridMailSettingsSandbox)
  implicit val sendGridMailSettingsSpamCheck = jsonFormat3(SendGridMailSettingsSpamCheck)
  implicit val sendGridMailSettings = jsonFormat5(SendGridMailSettings)
  implicit val sendGridClickTracking = jsonFormat2(SendGridClickTracking)
  implicit val sendGridOpenTracking = jsonFormat2(SendGridOpenTracking)
  implicit val sendGridSubscriptionTracking = jsonFormat4(SendGridSubscriptionTracking)
  implicit val sendGridGAnalyticsTracking = jsonFormat6(SendGridGAnalyticsTracking)
  implicit val sendGridTrackingSettings = jsonFormat4(SendGridTrackingSettings)
  implicit val sendGridRequest = jsonFormat17(SendGridRequest)

  implicit val sendGridError = jsonFormat3(SendGridError)
  implicit val sendGridErrorResponse = jsonFormat1(SendGridErrorResponse)
}

// https://sendgrid.com/docs/API_Reference/Web_API_v3/Mail/index.html
class SendGridService(apiToken: String) extends Directives with JsonSupport {

  implicit val system = ActorSystem("omg-sendgrid-client")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  val sg = new SendGrid(apiToken)

  val exceptionHandler = ExceptionHandler {
    case e: IOException =>
        val msg = e.getMessage
        // check whether the error comes from sendgrid and is due to bad user input
        val ioErrorStart = "Request returned status Code 400Body:"
        if (msg.startsWith(ioErrorStart)) {
          val j = msg.substring(ioErrorStart.length()).parseJson.convertTo[SendGridErrorResponse]
          val errorMsg = j.errors.getOrElse(List()).map(e => e.message).mkString(",")
          complete((BadRequest, ErrorMessage(false, errorMsg)))
        } else {
          complete((InternalServerError, ErrorMessage(false, s"Internal error: ${e.getMessage}")))
        }
    case e: Exception =>
      extractUri { uri =>
        println(s"Request to $uri failed: " + e.toString)
        complete((InternalServerError, ErrorMessage(false, s"Internal error: ${e.getMessage}")))
      }
  }

  val rejectionHandler = RejectionHandler.newBuilder()
    .handle { case MalformedRequestContentRejection(msg, _) =>
      complete((BadRequest, ErrorMessage(false, msg)))
    }
    .handleAll[MethodRejection] { methodRejections =>
      val names = methodRejections.map(_.supported.name)
      complete((MethodNotAllowed, ErrorMessage(false, s"Invalid method! Supported: ${names mkString " or "}!")))
    }
    .handleNotFound { complete((NotFound, ErrorMessage(false, "Invalid route."))) }
    .result()

  // return the message id or the error message
  def handleSGResponse(response: Response, requestContext: RequestContext) : StandardRoute = {
    if (response.getStatusCode() / 100 == 2) {
      val headers = response.getHeaders().asScala
      complete(UserResponse(headers.getOrElse("X-Message-Id", "ERROR")))
    } else {
      complete(ErrorMessage(true, response.getBody()))
    }
  }

  val route =
    handleRejections(rejectionHandler) {
      handleExceptions(exceptionHandler) {
        extractRequestContext { ctx =>
          path("send_one") {
            post {
              entity(as[EmailRequest]) { email =>
                val content = new Content(email.content_type getOrElse "text/plain", email.content)
                val mail = new Mail(new Email(email.from), email.subject, new Email(email.to), content)
                val request = new Request()
                request.setMethod(Method.POST)
                request.setEndpoint("mail/send")
                request.setBody(mail.build())
                handleSGResponse(sg.api(request), ctx)
              }
            }
          } ~
          path("send_many") {
            post {
              entity(as[SendGridRequest]) { email =>
                  val request = new Request();
                  request.setMethod(Method.POST);
                  request.setEndpoint("mail/send");
                  request.setBody(email.toJson.compactPrint)
                  handleSGResponse(sg.api(request), ctx)
              }
            }
          }
        }
      }
    }
}

object SendGridApp {
  def main(args: Array[String]) {

    implicit val system = ActorSystem("omg-sendgrid")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    if (!sys.env.contains("SENDGRID_API_TOKEN")) {
      println("No SENDGRID_API_TOKEN provided.")
      system.terminate()
      System.exit(1)
    }

    val apiToken =  sys.env("SENDGRID_API_TOKEN")
    val sendgridService = new SendGridService(apiToken)
    val bindingFuture = Http().bindAndHandle(sendgridService.route, "localhost", 8080)

    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}
