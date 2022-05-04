package glsf

import com.google.api.core.ApiFuture
import com.google.cloud.firestore.{FieldValue, Firestore, QuerySnapshot}
import com.typesafe.scalalogging.LazyLogging
import zio.Task
import zio.blocking.Blocking

import java.util as ju
import javax.inject.{Inject, Singleton}
import scala.jdk.CollectionConverters.*

@Singleton
class FirestoreUserRepository @Inject() (
    firestore: Firestore,
    blocking: Blocking.Service
) extends UserRepository
    with LazyLogging {
  private lazy val collection = firestore.collection("users")

  private def documentId(user: User): String =
    documentId(user.teamId, user.userId)

  private def documentId(teamId: String, userId: String): String =
    s"$teamId-$userId"

  private def userToMap(user: User): ju.Map[String, AnyRef] =
    Map(
      "teamId" -> user.teamId,
      "userId" -> user.userId,
      "mail" -> user.mail,
      "timestamp" -> FieldValue.serverTimestamp()
    ).asJava

  private def mapToUser(map: ju.Map[String, AnyRef]): Option[User] = {
    val m = map.asScala
    (m.get("teamId"), m.get("userId"), m.get("mail")) match {
      case (Some(teamId: String), Some(userId: String), Some(mail: String)) =>
        Some(User(teamId, userId, mail))
      case _ => None
    }
  }

  override def findBy(teamId: String, userId: String): Task[Option[User]] =
    blocking.effectBlocking {
      val snapshot = collection.document(documentId(teamId, userId)).get().get()
      if (snapshot.exists()) {
        mapToUser(snapshot.getData)
      } else {
        None
      }
    }

  override def findBy(mail: String): Task[Option[User]] =
    blocking.effectBlocking {
      val query = collection.whereEqualTo("mail", mail).get()
      extractUser(query)
    }

  private def extractUser(query: ApiFuture[QuerySnapshot]): Option[User] = {
    val docs = query.get().getDocuments.asScala
    if (docs.length > 1) {
      None
    } else {
      docs.headOption.map { s =>
        val map = s.getData
        mapToUser(map).getOrElse(sys.error(s"Failed to restore User: $map"))
      }
    }
  }

  override def store(user: User): Task[Unit] =
    blocking.effectBlocking {
      val doc = collection.document(documentId(user))
      doc.set(userToMap(user)).get()
    }
}
