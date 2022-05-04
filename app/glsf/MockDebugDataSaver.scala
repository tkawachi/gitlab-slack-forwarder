package glsf

import com.typesafe.scalalogging.StrictLogging
import zio.Task

class MockDebugDataSaver extends DebugDataSaver with StrictLogging {
  override def save(value: Map[String, String]): Task[Unit] = Task(
    logger.info(s"Saved $value")
  )
}
