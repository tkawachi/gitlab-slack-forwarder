package glsf
import scala.collection.mutable.ListBuffer
import scala.concurrent.Future

class MockUserRepository extends UserRepository {
  private[this] val m = ListBuffer.empty[User]

  override def findBy(teamId: String, userId: String): Future[Option[User]] =
    Future.successful(m.find(u => u.teamId == teamId && u.userId == userId))

  override def findBy(mail: String): Future[Option[User]] =
    Future.successful(m.find(u => u.mail == mail))

  override def store(user: User): Future[Unit] =
    Future.successful(m.addOne(user))
}
