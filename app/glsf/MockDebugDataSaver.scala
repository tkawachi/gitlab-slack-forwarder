package glsf

import com.typesafe.scalalogging.StrictLogging
import zio.{Task, ZIO}

class MockDebugDataSaver extends DebugDataSaver with StrictLogging {
  override def save(value: Map[String, String]): Task[Unit] = ZIO.attempt(
    logger.info(s"Saved $value")
  )
}
