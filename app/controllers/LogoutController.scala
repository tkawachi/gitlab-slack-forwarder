package controllers

import play.api.mvc.{
  AbstractController,
  Action,
  AnyContent,
  ControllerComponents
}

import javax.inject.Inject

class LogoutController @Inject() (cc: ControllerComponents)
    extends AbstractController(cc) {
  def logout: Action[AnyContent] =
    Action {
      Redirect(routes.HomeController.index()).withNewSession
    }
}
