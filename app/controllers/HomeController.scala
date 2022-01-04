package controllers

import glsf.{SlackConfig, TeamToken, TeamTokenRepository}
import play.api.mvc.*
import util.ResultCont

import javax.inject.*
import scala.concurrent.ExecutionContext

/** This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject() (
    val controllerComponents: ControllerComponents,
    slackConfig: SlackConfig,
    authentication: Authentication,
    teamTokenRepository: TeamTokenRepository,
    implicit val ec: ExecutionContext
) extends BaseController {

  private def findTeamToken(teamId: String): ResultCont[Option[TeamToken]] =
    ResultCont.fromFuture(teamTokenRepository.findBy(teamId))

  /** Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method will be
    * called when the application receives a `GET` request with a path of `/`.
    */
  def index(): Action[AnyContent] =
    Action.async { implicit request: Request[AnyContent] =>
      (for {
        maybeUser <- authentication.auth(request)
        maybeTeamToken <- maybeUser.fold(
          ResultCont.pure(None: Option[TeamToken])
        )(u => findTeamToken(u.teamId))
      } yield Ok(
        views.html
          .index(
            maybeUser,
            maybeTeamToken,
            slackConfig.clientId,
            slackConfig.signInRedirectUri,
            slackConfig.addRedirectUri
          )
      )).run_
    }

  def privacyPolicy(): Action[AnyContent] =
    Action { Ok(views.html.privacyPolicy()) }
}
