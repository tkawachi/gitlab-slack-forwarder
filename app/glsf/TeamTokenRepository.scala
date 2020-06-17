package glsf

import scala.concurrent.Future

trait TeamTokenRepository {
  def findBy(teamId: String): Future[Option[TeamToken]]
  def store(teamToken: TeamToken): Future[Unit]
}
