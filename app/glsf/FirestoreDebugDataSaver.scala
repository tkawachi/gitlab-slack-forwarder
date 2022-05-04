package glsf

import com.google.cloud.firestore.Firestore
import zio.Task
import zio.blocking.Blocking

import javax.inject.{Inject, Singleton}
import scala.jdk.CollectionConverters.*

@Singleton
class FirestoreDebugDataSaver @Inject() (
    firestore: Firestore,
    blocking: Blocking.Service
) extends DebugDataSaver {
  private[this] val data = firestore.collection("data")

  override def save(value: Map[String, String]): Task[Unit] =
    blocking.effectBlocking {
      data.add(value.asJava).get()
    }
}
