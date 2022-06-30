package controllers

import glsf.{SlackConfig, TeamToken, TeamTokenRepository}
import play.api.mvc.*
import zio.{Runtime, Task, Unsafe, ZIO}

import javax.inject.*

/** This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject() (
    val controllerComponents: ControllerComponents,
    slackConfig: SlackConfig,
    authentication: Authentication,
    teamTokenRepository: TeamTokenRepository,
    runtime: Runtime[Any]
) extends BaseController {

  private def findTeamToken(teamId: String): Task[Option[TeamToken]] =
    teamTokenRepository.findBy(teamId)

  /** Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method will be
    * called when the application receives a `GET` request with a path of `/`.
    */
  def index(): Action[AnyContent] =
    Action.async { implicit request: Request[AnyContent] =>
      val io = for {
        maybeUser <- authentication.auth(request)
        maybeTeamToken <- maybeUser
          .map(u => findTeamToken(u.teamId))
          .getOrElse(ZIO.none)
      } yield Ok(
        views.html
          .index(
            maybeUser,
            maybeTeamToken,
            slackConfig.clientId,
            slackConfig.signInRedirectUri,
            slackConfig.addRedirectUri
          )
      )
      Unsafe.unsafe { implicit u =>
        runtime.unsafe.runToFuture(io)
      }
    }

  def privacyPolicy(): Action[AnyContent] =
    Action { Ok(views.html.privacyPolicy()) }
}
