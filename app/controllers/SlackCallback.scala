package controllers

import com.typesafe.scalalogging.LazyLogging
import glsf.*
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSClient
import play.api.mvc.*
import util.ResultCont

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext
import scala.util.control.NonFatal

@Singleton
class SlackCallback @Inject() (
    cc: ControllerComponents,
    ws: WSClient,
    slackConfig: SlackConfig,
    mailGenerator: MailGenerator,
    userRepository: UserRepository,
    teamTokenRepository: TeamTokenRepository,
    implicit val ec: ExecutionContext
) extends AbstractController(cc)
    with LazyLogging {

  private def getCode(request: Request[_]): ResultCont[String] =
    ResultCont.fromOption(request.getQueryString("code"))(
      BadRequest(
        views.html
          .error("Failed to sign in Slack", "Failed to sign in Slack")
      )
    )

  private def getAccessJson(
      code: String,
      redirectUri: String
  ): ResultCont[JsValue] = {
    ResultCont
      .fromFuture(
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
      .flatMap { resp =>
        if (resp.status != 200) {
          ResultCont.result(
            BadRequest(
              views.html.error("Slack error", "Slack response is not 200")
            )
          )
        } else {
          try {
            ResultCont.pure(Json.parse(resp.body))
          } catch {
            case NonFatal(e) =>
              ResultCont.result(
                BadRequest(
                  views.html.error("Slack error", "Slack returns invalid JSON")
                )
              )
          }
        }
      }
  }

  private def checkOk(json: JsValue): ResultCont[Unit] =
    try {
      val ok = (json \ "ok").as[Boolean]
      if (!ok) {
        logger.info(s"ok=false $json")
        ResultCont.result(
          BadRequest(
            views.html.error("Slack error", "Slack response ok = false")
          )
        )
      } else {
        ResultCont.pure(())
      }
    } catch {
      case NonFatal(_) =>
        ResultCont.result(
          BadRequest(
            views.html
              .error("Slack error", "Slack response doesn't contain 'ok'")
          )
        )
    }

  private val createMailMaxRetry = 20

  private def createMail(retry: Int = 0): ResultCont[String] = {
    val m = mailGenerator.generate()
    ResultCont.fromFuture(userRepository.findBy(m)).flatMap {
      case Some(_) =>
        if (retry > createMailMaxRetry) {
          logger.error("Failed to create unique mail")
          ResultCont.result(InternalServerError)
        } else {
          createMail(retry + 1)
        }
      case None => ResultCont.pure(m)
    }
  }

  private def getOrCreateUser(
      teamId: String,
      userId: String
  ): ResultCont[User] = {
    ResultCont.fromFuture(userRepository.findBy(teamId, userId)).flatMap {
      case Some(user) =>
        ResultCont.pure(user)
      case None =>
        createMail().flatMap { mail =>
          val user = User(teamId, userId, mail)
          logger.info(s"Creating new user: $user")
          ResultCont.fromFuture(userRepository.store(user)).map(_ => user)
        }
    }
  }

  def signIn: Action[AnyContent] =
    Action.async { request =>
      (for {
        code <- getCode(request)
        json <- getAccessJson(code, slackConfig.signInRedirectUri)
        _ <- checkOk(json)
        userId = (json \ "authed_user" \ "id").as[String]
        teamId = (json \ "team" \ "id").as[String]
        user <- getOrCreateUser(teamId, userId)
      } yield {
        Redirect(routes.HomeController.index()).withNewSession
          .withSession("teamId" -> user.teamId, "userId" -> user.userId)
      }).run_
    }

  def add: Action[AnyContent] =
    Action.async { request =>
      (for {
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
        _ <- ResultCont.fromFuture(teamTokenRepository.store(teamToken))
      } yield Redirect(routes.HomeController.index())).run_
    }
}
