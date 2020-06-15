package controllers

import javax.inject.Inject
import play.api.mvc.{
  AbstractController,
  Action,
  AnyContent,
  ControllerComponents
}

class LogoutController @Inject()(cc: ControllerComponents)
    extends AbstractController(cc) {
  def logout: Action[AnyContent] = Action {
    Redirect(routes.HomeController.index()).withNewSession
  }
}
