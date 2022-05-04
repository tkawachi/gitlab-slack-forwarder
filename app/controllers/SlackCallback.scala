package controllers

import com.typesafe.scalalogging.LazyLogging
import glsf.*
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSClient
import play.api.mvc.*
import zio.{IO, Runtime, Task, ZEnv, ZIO}

import javax.inject.{Inject, Singleton}

@Singleton
class SlackCallback @Inject() (
    cc: ControllerComponents,
    ws: WSClient,
    slackConfig: SlackConfig,
    mailGenerator: MailGenerator,
    userRepository: UserRepository,
    teamTokenRepository: TeamTokenRepository,
    runtime: Runtime[ZEnv]
) extends AbstractController(cc)
    with LazyLogging {

  private def getCode(request: Request[_]): IO[Result, String] =
    ZIO
      .fromOption(request.getQueryString("code"))
      .mapError(_ =>
        BadRequest(
          views.html
            .error("Failed to sign in Slack", "Failed to sign in Slack")
        )
      )

  private def getAccessJson(
      code: String,
      redirectUri: String
  ): IO[Result, JsValue] = {
    zio.Task
      .fromFuture(_ =>
        ws.url(slackConfig.accessUrl)
          .post(
            Map(
              "code" -> Seq(code),
              "client_id" -> Seq(slackConfig.clientId),
              "client_secret" -> Seq(slackConfig.clientSecret),
              "redirect_uri" -> Seq(redirectUri)
            )
          )
      )
      .mapError { e =>
        logger.error("failed to access slack", e)
        InternalServerError(
          views.html.error("Slack error", "Failed to access slack")
        )
      }
      .filterOrFail(_.status == 200)(
        BadRequest(
          views.html.error("Slack error", "Slack response is not 200")
        )
      )
      .flatMap { resp =>
        Task(Json.parse(resp.body))
          .mapError { e =>
            logger.error("Slack returns invalid JSON", e)
            BadRequest(
              views.html.error("Slack error", "Slack returns invalid JSON")
            )
          }
      }
  }

  private def checkOk(json: JsValue): IO[Result, Unit] =
    Task((json \ "ok").as[Boolean])
      .mapError { e =>
        logger.warn("Slack response doesn't contain 'ok'", e)
        BadRequest(
          views.html
            .error("Slack error", "Slack response doesn't contain 'ok'")
        )
      }
      .filterOrFail(identity) {
        logger.info(s"ok=false $json")
        BadRequest(
          views.html.error("Slack error", "Slack response ok = false")
        )
      }
      .unit

  private val createMailMaxRetry = 20

  private def createMail(retry: Int = 0): IO[Result, String] = {
    mailGenerator.generate().flatMap { m =>
      userRepository
        .findBy(m)
        .mapError { e =>
          logger.error(s"Failed findBy $m", e)
          InternalServerError
        }
        .flatMap {
          case Some(_) =>
            if (retry > createMailMaxRetry) {
              logger.error("Failed to create unique mail")
              ZIO.fail(InternalServerError)
            } else {
              createMail(retry + 1)
            }
          case None => ZIO.succeed(m)
        }
    }
  }

  private def getOrCreateUser(
      teamId: String,
      userId: String
  ): IO[Result, User] = {
    userRepository
      .findBy(teamId, userId)
      .mapError { e =>
        logger.error("UserRepository.findBy", e)
        InternalServerError
      }
      .flatMap {
        case Some(user) =>
          ZIO.succeed(user)
        case None =>
          createMail().flatMap { mail =>
            val user = User(teamId, userId, mail)
            logger.info(s"Creating new user: $user")
            userRepository
              .store(user)
              .map(_ => user)
              .mapError { e =>
                logger.error("UserRepository.store", e)
                InternalServerError
              }
          }
      }
  }

  def signIn: Action[AnyContent] =
    Action.async { request =>
      runtime.unsafeRunToFuture((for {
        code <- getCode(request)
        json <- getAccessJson(code, slackConfig.signInRedirectUri)
        _ <- checkOk(json)
        userId = (json \ "authed_user" \ "id").as[String]
        teamId = (json \ "team" \ "id").as[String]
        user <- getOrCreateUser(teamId, userId)
      } yield {
        Redirect(routes.HomeController.index()).withNewSession
          .withSession("teamId" -> user.teamId, "userId" -> user.userId)
      }).merge)
    }

  def add: Action[AnyContent] =
    Action.async { request =>
      runtime.unsafeRunToFuture((for {
        code <- getCode(request)
        json <- getAccessJson(code, slackConfig.addRedirectUri)
        _ <- checkOk(json)
        teamId = (json \ "team" \ "id").as[String]
        teamName = (json \ "team" \ "name").as[String]
        botAccessToken = (json \ "access_token").as[String]
        scope = (json \ "scope").as[String]
        botUserId = (json \ "bot_user_id").as[String]
        teamToken =
          TeamToken(teamId, teamName, scope, botUserId, botAccessToken)
        _ =
          logger.info(s"Storing TeamToken: $teamId $teamName $scope $botUserId")
        _ <- teamTokenRepository.store(teamToken).mapError { e =>
          logger.error("TeamTokenRepository.store", e)
          InternalServerError
        }
      } yield Redirect(routes.HomeController.index())).merge)
    }
}
