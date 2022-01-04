package controllers

import com.slack.api.Slack
import com.slack.api.methods.request.chat.ChatPostMessageRequest
import com.typesafe.scalalogging.LazyLogging
import glsf.format.{MailMessage, MessageFormatter, SlackMessage}
import glsf.{DebugDataSaver, TeamTokenRepository, User, UserRepository}
import play.api.libs.Files
import play.api.libs.json.Json
import play.api.mvc.{
  AbstractController,
  Action,
  ControllerComponents,
  MultipartFormData
}
import util.ResultCont

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters.*

@Singleton
class ForwardController @Inject() (
    cc: ControllerComponents,
    userRepository: UserRepository,
    teamTokenRepository: TeamTokenRepository,
    debugDataSaver: DebugDataSaver,
    messageFormatter: MessageFormatter,
    implicit val ec: ExecutionContext,
    @Named("io") ioec: ExecutionContext
) extends AbstractController(cc)
    with LazyLogging {

  private val slack = Slack.getInstance()

  private def findUser(to: String): ResultCont[User] =
    ResultCont.fromFuture(userRepository.findBy(to)).getOrResult {
      logger.info(s"Message come to unknown mail: $to")
      Ok
    }

  private def notifySlack(
      user: User,
      sm: SlackMessage
  ): ResultCont[Unit] =
    ResultCont
      .fromFuture(teamTokenRepository.findBy(user.teamId))
      .getOrResult {
        logger.info(s"Not found teamId ${user.teamId}")
        NotFound
      }
      .flatMap { teamToken =>
        ResultCont.fromFuture(Future {
          logger.info(s"Send messag to Slack: $user")
          val m = slack.methods(teamToken.botAccessToken)
          val message = ChatPostMessageRequest
            .builder()
            .channel(user.userId)
            .text(sm.mrkdwn)
            .mrkdwn(true)
            .blocks(sm.blocks.asJava)
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

  def formatMessage(message: MailMessage): ResultCont[SlackMessage] = {
    messageFormatter.format(message) match {
      case Some(blocks) => ResultCont.pure(blocks)
      case None =>
        ResultCont
          .fromFuture(
            // Store unknown format message
            debugDataSaver
              .save(Map("mail" -> Json.toJson(message.dataParts).toString()))
          )
          .map { _ =>
            messageFormatter.defaultFallback(message)
          }
    }
  }

  def post: Action[MultipartFormData[Files.TemporaryFile]] =
    Action.async(cc.parsers.multipartFormData) { implicit request =>
      // ref. https://sendgrid.com/docs/for-developers/parsing-email/setting-up-the-inbound-parse-webhook/
      val data = request.body.dataParts
      val message = MailMessage(data)
      (for {
        tos <- parseEnvelopeTo(data)
        user <- findUser(tos.head) // TODO
        slackMessage <- formatMessage(message)
        _ <- notifySlack(user, slackMessage)
      } yield Ok).run_
    }
}
