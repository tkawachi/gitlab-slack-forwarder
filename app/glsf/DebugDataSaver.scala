package glsf

import zio.Task

trait DebugDataSaver {
  def save(value: Map[String, String]): Task[Unit]
}
