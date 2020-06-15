package controllers

import glsf.{SlackConfig, User}
import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc.{
  AbstractController,
  Action,
  AnyContent,
  ControllerComponents
}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SlackCallback @Inject()(cc: ControllerComponents,
                              ws: WSClient,
                              slackConfig: SlackConfig,
                              implicit val ec: ExecutionContext)
    extends AbstractController(cc) {
  def callback: Action[AnyContent] = Action.async { request =>
    request.getQueryString("code") match {
      case Some(code) =>
        ws.url(slackConfig.accessUrl)
          .post(
            Map(
              "code" -> Seq(code),
              "client_id" -> Seq(slackConfig.clientId),
              "client_secret" -> Seq(slackConfig.clientSecret),
              "redirect_uri" -> Seq(slackConfig.redirectUri)
            )
          )
          .map { resp =>
            if (resp.status != 200) {
              BadRequest(
                views.html.error("Slack error", "Slack response is not 200")
              )
            } else {
              val json = Json.parse(resp.body)
              val ok = (json \ "ok").as[Boolean]
              if (!ok) {
                BadRequest(
                  views.html.error("Slack error", "Slack response ok = false")
                )
              } else {
                val userId = (json \ "authed_user" \ "id").as[String]
                // (json \ "authed_user" \ "access_token").as[String]
                val teamId = (json \ "team" \ "id").as[String]
//                val user = User(teamId, userId)
//                Ok(s"${resp.status} ${resp.body}").withNewSession
//                  .withSession("teamId" -> teamId, "userId" -> userId)
                Redirect(routes.HomeController.index()).withNewSession
                  .withSession("teamId" -> teamId, "userId" -> userId)
              }
            }
          }
      case None =>
        Future.successful(
          BadRequest(
            views.html
              .error("Failed to sign in Slack", "Failed to sign in Slack")
          )
        )
    }
  }
}
