package controllers

import com.slack.api.Slack
import com.slack.api.methods.request.chat.ChatPostMessageRequest
import com.typesafe.scalalogging.LazyLogging
import glsf.{TeamTokenRepository, User, UserRepository}
import javax.inject.{Inject, Named, Singleton}
import play.api.libs.Files
import play.api.libs.json.Json
import play.api.mvc.{
  AbstractController,
  Action,
  ControllerComponents,
  MultipartFormData
}
import util.ResultCont

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ForwardController @Inject()(cc: ControllerComponents,
                                  userRepository: UserRepository,
                                  teamTokenRepository: TeamTokenRepository,
                                  implicit val ec: ExecutionContext,
                                  @Named("io") ioec: ExecutionContext)
    extends AbstractController(cc)
    with LazyLogging {

  private val slack = Slack.getInstance()

  private def findUser(to: String): ResultCont[User] =
    ResultCont.fromFuture(userRepository.findBy(to)).getOrResult {
      logger.info(s"Message come to unknown mail: $to")
      Ok
    }

  private def notifySlack(user: User,
                          subject: String,
                          text: String): ResultCont[Unit] =
    ResultCont
      .fromFuture(teamTokenRepository.findBy(user.teamId))
      .getOrResult {
        logger.info(s"Not found teamId ${user.teamId}")
        NotFound
      }
      .flatMap { teamToken =>
        ResultCont.fromFuture(Future {
          logger.info(s"Send messag to Slack: $user $subject $text")
          val m = slack.methods(teamToken.botAccessToken)
          val message = ChatPostMessageRequest
            .builder()
            .channel(user.userId)
            .text(subject + "\n" + text)
            .build()
          val resp = m.chatPostMessage(message)
          if (!resp.isOk) {
            logger.error(resp.toString)
          }
        }(ioec))
      }

  def parseEnvelopeTo(
    data: Map[String, Seq[String]]
  ): ResultCont[Seq[String]] = {
    for {
      envelopes <- ResultCont.fromOption(data.get("envelope")) {
        logger.info(s"envelope not found: $data")
        BadRequest("envelope not found")
      }
      envelope <- ResultCont.fromOption(envelopes.headOption) {
        logger.info(s"envelope is empty: $data")
        BadRequest("envelope is empty")
      }
      tos <- ResultCont.pure((Json.parse(envelope) \ "to").as[Seq[String]])
    } yield tos
  }

  def post: Action[MultipartFormData[Files.TemporaryFile]] =
    Action.async(cc.parsers.multipartFormData) { implicit request =>
      // ref. https://sendgrid.com/docs/for-developers/parsing-email/setting-up-the-inbound-parse-webhook/
      val data = request.body.dataParts
      (for {
        tos <- parseEnvelopeTo(data)
        user <- findUser(tos.head) // TODO
        _ <- notifySlack(user, data("subject").head, data("text").head)
      } yield Ok).run_
    }
}
