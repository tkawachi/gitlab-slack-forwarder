package controllers

import com.typesafe.scalalogging.LazyLogging
import glsf.{MailGenerator, User, UserRepository}
import javax.inject.Inject
import play.api.mvc.Request
import util.ResultCont
import play.api.mvc.Results._

import scala.concurrent.ExecutionContext

class Authentication @Inject()(userRepository: UserRepository,
                               mailGenerator: MailGenerator,
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

  private def createUser(teamId: String, userId: String): ResultCont[User] = {
    val mail = mailGenerator.generate()
    val user = User(teamId, userId, mail)
    logger.info(s"Creating new user: $user")
    ResultCont.fromFuture(userRepository.store(user)).map(_ => user)
  }

  def auth(request: Request[_]): ResultCont[Option[User]] =
    for {
      maybeIds <- auth2(request)
      maybeUser <- maybeIds match {
        case Some((teamId, userId)) =>
          findUser(teamId, userId).flatMap {
            case Some(existingUser) => ResultCont.pure(Option(existingUser))
            case None               => createUser(teamId, userId).map(Option.apply)
          }
        case None =>
          ResultCont.pure(None)
      }
    } yield maybeUser
}
