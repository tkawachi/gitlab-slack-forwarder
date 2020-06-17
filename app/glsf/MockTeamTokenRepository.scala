package glsf
import javax.inject.Singleton

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future

@Singleton
class MockTeamTokenRepository extends TeamTokenRepository {
  private[this] val tokens = ListBuffer.empty[TeamToken]

  override def findBy(teamId: String): Future[Option[TeamToken]] =
    Future.successful(tokens.find(t => t.teamId == teamId))

  override def store(teamToken: TeamToken): Future[Unit] = Future.successful {
    tokens.addOne(teamToken)
    ()
  }
}
