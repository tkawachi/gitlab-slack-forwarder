package controllers

import com.slack.api.Slack
import com.slack.api.methods.request.chat.ChatPostMessageRequest
import com.typesafe.scalalogging.LazyLogging
import glsf.format.{MailMessage, MessageFormatter, SlackMessage}
import glsf.{DebugDataSaver, TeamTokenRepository, User, UserRepository}
import play.api.libs.Files
import play.api.libs.json.Json
import play.api.mvc.*
import zio.{Runtime, Task, ZEnv, ZIO}

import javax.inject.{Inject, Singleton}
import scala.jdk.CollectionConverters.*

@Singleton
class ForwardController @Inject() (
    cc: ControllerComponents,
    userRepository: UserRepository,
    teamTokenRepository: TeamTokenRepository,
    debugDataSaver: DebugDataSaver,
    messageFormatter: MessageFormatter,
    runtime: Runtime[ZEnv]
) extends AbstractController(cc)
    with LazyLogging {

  private val slack = Slack.getInstance()

  private def findUser(to: String): ZIO[Any, Result, User] =
    userRepository
      .findBy(to)
      .mapError { e =>
        logger.error("UserRepository.findBy", e)
        InternalServerError
      }
      .someOrFail {
        logger.info(s"Message come to unknown mail: $to")
        Ok
      }

  private def notifySlack(
      user: User,
      sm: SlackMessage
  ): ZIO[Any, Result, Unit] =
    teamTokenRepository
      .findBy(user.teamId)
      .mapError { e =>
        logger.error("Failed to find user", e)
        InternalServerError
      }
      .someOrFail {
        logger.info(s"Not found teamId ${user.teamId}")
        NotFound
      }
      .map { teamToken =>
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
      }

  def parseEnvelopeTo(
      data: Map[String, Seq[String]]
  ): ZIO[Any, Result, Seq[String]] = {
    for {
      envelopes <- ZIO.fromOption(data.get("envelope")).mapError { _ =>
        logger.info(s"envelope not found: $data")
        BadRequest("envelope not found")
      }
      envelope <- ZIO.fromOption(envelopes.headOption).mapError { _ =>
        logger.info(s"envelope is empty: $data")
        BadRequest("envelope is empty")
      }
      tos <- Task((Json.parse(envelope) \ "to").as[Seq[String]]).mapError { e =>
        logger.info(s"envelope is not a json", e)
        BadRequest("Invalid json")
      }
    } yield tos
  }

  def formatMessage(message: MailMessage): ZIO[Any, Result, SlackMessage] = {
    messageFormatter.format(message) match {
      case Some(blocks) => ZIO.succeed(blocks)
      case None         =>
        // Store unknown format message
        debugDataSaver
          .save(Map("mail" -> Json.toJson(message.dataParts).toString()))
          .mapError { e =>
            logger.error("Failed to save", e)
            InternalServerError
          }
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
      runtime.unsafeRunToFuture((for {
        tos <- parseEnvelopeTo(data)
        user <- findUser(tos.head) // TODO
        slackMessage <- formatMessage(message)
        _ <- notifySlack(user, slackMessage)
      } yield Ok("")).merge)
    }
}
