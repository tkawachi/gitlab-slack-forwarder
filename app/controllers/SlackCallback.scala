package controllers

import glsf.SlackConfig
import javax.inject.{Inject, Singleton}
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSClient
import play.api.mvc._
import util.ResultCont

import scala.concurrent.ExecutionContext
import scala.util.control.NonFatal

@Singleton
class SlackCallback @Inject()(cc: ControllerComponents,
                              ws: WSClient,
                              slackConfig: SlackConfig,
                              implicit val ec: ExecutionContext)
    extends AbstractController(cc) {

  private def getCode(request: Request[_]): ResultCont[String] =
    ResultCont.fromOption(request.getQueryString("code"))(
      BadRequest(
        views.html
          .error("Failed to sign in Slack", "Failed to sign in Slack")
      )
    )

  private def getAccessJson(code: String): ResultCont[JsValue] = {
    ResultCont
      .fromFuture(
        ws.url(slackConfig.accessUrl)
          .post(
            Map(
              "code" -> Seq(code),
              "client_id" -> Seq(slackConfig.clientId),
              "client_secret" -> Seq(slackConfig.clientSecret),
              "redirect_uri" -> Seq(slackConfig.redirectUri)
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

  def callback: Action[AnyContent] = Action.async { request =>
    (for {
      code <- getCode(request)
      json <- getAccessJson(code)
      _ <- checkOk(json)
    } yield {
      val userId = (json \ "authed_user" \ "id").as[String]
      // (json \ "authed_user" \ "access_token").as[String]
      val teamId = (json \ "team" \ "id").as[String]
      //                val user = User(teamId, userId)
      //                Ok(s"${resp.status} ${resp.body}").withNewSession
      //                  .withSession("teamId" -> teamId, "userId" -> userId)
      Redirect(routes.HomeController.index()).withNewSession
        .withSession("teamId" -> teamId, "userId" -> userId)
    }).run_
  }
}
