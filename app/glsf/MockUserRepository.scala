package glsf

import zio.{Task, ZIO}

import javax.inject.Singleton
import scala.collection.mutable.ListBuffer

@Singleton
class MockUserRepository extends UserRepository {
  private[this] val m = ListBuffer.empty[User]

  override def findBy(teamId: String, userId: String): Task[Option[User]] =
    ZIO.succeed(m.find(u => u.teamId == teamId && u.userId == userId))

  override def findBy(mail: String): Task[Option[User]] =
    ZIO.succeed(m.find(u => u.mail == mail))

  override def store(user: User): Task[Unit] =
    ZIO.succeed(m.addOne(user))
}
