package glsf

import scala.concurrent.Future

trait DebugDataSaver {
  def save(value: Map[String, String]): Future[Unit]
}
