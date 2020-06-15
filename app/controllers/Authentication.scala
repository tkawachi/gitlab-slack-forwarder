package controllers

import glsf.User
import javax.inject.Inject
import play.api.mvc.Request
import util.ResultCont

class Authentication @Inject()() {
  def auth(request: Request[_]): ResultCont[Option[User]] =
    ResultCont.pure {
      for {
        teamId <- request.session.get("teamId")
        userId <- request.session.get("userId")
      } yield User(teamId, userId)
    }
}
