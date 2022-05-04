package glsf

import zio.Task

trait TeamTokenRepository {
  def findBy(teamId: String): Task[Option[TeamToken]]

  def store(teamToken: TeamToken): Task[Unit]
}
