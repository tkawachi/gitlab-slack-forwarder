package glsf

import scala.concurrent.Future

trait UserRepository {
  def findBy(teamId: String, userId: String): Future[Option[User]]
  def findBy(mail: String): Future[Option[User]]
  def store(user: User): Future[Unit]
}
