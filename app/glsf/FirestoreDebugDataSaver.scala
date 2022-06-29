package glsf

import com.google.cloud.firestore.Firestore
import zio.{Task, ZIO}

import javax.inject.{Inject, Singleton}
import scala.jdk.CollectionConverters.*

@Singleton
class FirestoreDebugDataSaver @Inject() (
    firestore: Firestore
) extends DebugDataSaver {
  private[this] val data = firestore.collection("data")

  override def save(value: Map[String, String]): Task[Unit] =
    ZIO.attemptBlocking {
      data.add(value.asJava).get()
    }
}
