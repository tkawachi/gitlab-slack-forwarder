package controllers

import glsf.{AppConfig, SlackConfig, User}
import javax.inject._
import play.api.mvc._
import util.ResultCont

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents,
                               slackConfig: SlackConfig,
                               authentication: Authentication)
    extends BaseController {

  /**
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def index(): Action[AnyContent] = Action.async {
    implicit request: Request[AnyContent] =>
      authentication
        .auth(request)
        .map { maybeUser =>
          Ok(
            views.html
              .index(maybeUser, slackConfig.clientId, slackConfig.redirectUri)
          )
        }
        .run_
  }
}
