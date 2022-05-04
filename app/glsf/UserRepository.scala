package glsf

import zio.Task

trait UserRepository {
  def findBy(teamId: String, userId: String): Task[Option[User]]

  def findBy(mail: String): Task[Option[User]]

  def store(user: User): Task[Unit]
}
