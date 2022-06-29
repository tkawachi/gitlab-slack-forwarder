package controllers

import com.typesafe.scalalogging.LazyLogging
import glsf.{User, UserRepository}
import play.api.mvc.Request
import zio.{Task, ZIO}

import javax.inject.Inject

class Authentication @Inject() (
    userRepository: UserRepository
) extends LazyLogging {
  private def auth2(request: Request[_]): Option[(String, String)] =
    for {
      teamId <- request.session.get("teamId")
      userId <- request.session.get("userId")
    } yield (teamId, userId)

  def auth(request: Request[_]): Task[Option[User]] =
    for {
      maybeIds <- ZIO.succeed(auth2(request))
      maybeUser <-
        maybeIds
          .map { case (teamId, userId) =>
            userRepository.findBy(teamId, userId)
          }
          .getOrElse(ZIO.none)
    } yield maybeUser
}
