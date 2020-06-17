package controllers

import com.slack.api.Slack
import com.slack.api.methods.request.chat.ChatPostMessageRequest
import com.slack.api.methods.request.chat.ChatPostMessageRequest.ChatPostMessageRequestBuilder
import com.typesafe.scalalogging.LazyLogging
import glsf.{TeamTokenRepository, User, UserRepository}
import javax.inject.{Inject, Singleton}
import play.api.libs.Files
import play.api.mvc.{
  AbstractController,
  Action,
  AnyContent,
  ControllerComponents,
  MultipartFormData
}
import util.ResultCont

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ForwardController @Inject()(cc: ControllerComponents,
                                  userRepository: UserRepository,
                                  teamTokenRepository: TeamTokenRepository,
                                  implicit val ec: ExecutionContext)
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
        ResultCont.pure {
          logger.info(s"Send messag to Slack: $user $subject $text")
          val m = slack.methods(teamToken.botAccessToken)
          val message = ChatPostMessageRequest
            .builder()
            .channel(user.userId)
            .text(subject + "\n" + text)
            .build()
          val resp = m.chatPostMessage(message) // TODO change ExecutionContext
          if (!resp.isOk) {
            logger.error(resp.toString)
          }
        }
      }

  def post: Action[MultipartFormData[Files.TemporaryFile]] =
    Action.async(cc.parsers.multipartFormData) { implicit request =>
      // ref. https://sendgrid.com/docs/for-developers/parsing-email/setting-up-the-inbound-parse-webhook/
      val data = request.body.dataParts
      (for {
        user <- findUser(data("to").head)
        _ <- notifySlack(user, data("subject").head, data("text").head)
      } yield Ok).run_
    }
}
