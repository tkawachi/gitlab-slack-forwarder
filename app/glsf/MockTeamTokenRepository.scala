package glsf

import zio.Task

import javax.inject.Singleton
import scala.collection.mutable.ListBuffer

@Singleton
class MockTeamTokenRepository extends TeamTokenRepository {
  private[this] val tokens = ListBuffer.empty[TeamToken]

  override def findBy(teamId: String): Task[Option[TeamToken]] =
    Task.succeed(tokens.find(t => t.teamId == teamId))

  override def store(teamToken: TeamToken): Task[Unit] =
    Task.succeed {
      tokens.addOne(teamToken)
      ()
    }
}
