package glsf

import zio.{Task, ZIO}

import javax.inject.Singleton
import scala.collection.mutable.ListBuffer

@Singleton
class MockTeamTokenRepository extends TeamTokenRepository {
  private[this] val tokens = ListBuffer.empty[TeamToken]

  override def findBy(teamId: String): Task[Option[TeamToken]] =
    ZIO.succeed(tokens.find(t => t.teamId == teamId))

  override def store(teamToken: TeamToken): Task[Unit] =
    ZIO.succeed {
      tokens.addOne(teamToken)
      ()
    }
}
