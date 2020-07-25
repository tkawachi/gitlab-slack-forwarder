package controllers

import com.typesafe.scalalogging.LazyLogging
import glsf.{MailGenerator, User, UserRepository}
import javax.inject.Inject
import play.api.mvc.Request
import util.ResultCont

import scala.concurrent.ExecutionContext

class Authentication @Inject()(userRepository: UserRepository,
                               implicit val ec: ExecutionContext)
    extends LazyLogging {
  private def auth2(request: Request[_]): ResultCont[Option[(String, String)]] =
    ResultCont.pure {
      for {
        teamId <- request.session.get("teamId")
        userId <- request.session.get("userId")
      } yield (teamId, userId)
    }

  private def findUser(teamId: String,
                       userId: String): ResultCont[Option[User]] =
    ResultCont.fromFuture(userRepository.findBy(teamId, userId))

  def auth(request: Request[_]): ResultCont[Option[User]] =
    for {
      maybeIds <- auth2(request)
      maybeUser <- maybeIds
        .map { case (teamId, userId) => findUser(teamId, userId) }
        .getOrElse(ResultCont.pure(None))
    } yield maybeUser
}
