package glsf

import com.google.cloud.firestore.Firestore
import javax.inject.{Inject, Named, Singleton}

import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters._

@Singleton
class FirestoreDebugDataSaver @Inject() (
    firestore: Firestore,
    @Named("io") implicit val ec: ExecutionContext
) extends DebugDataSaver {
  private[this] val data = firestore.collection("data")

  override def save(value: Map[String, String]): Future[Unit] =
    Future {
      data.add(value.asJava).get()
    }
}
