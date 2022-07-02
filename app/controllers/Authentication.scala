package controllers

import com.typesafe.scalalogging.LazyLogging
import glsf.{User, UserRepository}
import play.api.mvc.RequestHeader
import zio.{Task, ZIO}

import javax.inject.Inject

class Authentication @Inject() (
    userRepository: UserRepository
) extends LazyLogging {
  private def extractTeamIdUserId(
      request: RequestHeader
  ): Option[(String, String)] =
    for {
      teamId <- request.session.get("teamId")
      userId <- request.session.get("userId")
    } yield (teamId, userId)

  def auth(request: RequestHeader): Task[Option[User]] =
    for {
      maybeUser <- ZIO.foreach(extractTeamIdUserId(request)) {
        case (teamId, userId) => userRepository.findBy(teamId, userId)
      }
    } yield maybeUser.flatten
}
