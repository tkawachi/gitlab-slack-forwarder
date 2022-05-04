package glsf

import com.google.cloud.firestore.{FieldValue, Firestore}
import zio.Task
import zio.blocking.Blocking

import java.util as ju
import javax.inject.{Inject, Singleton}
import scala.jdk.CollectionConverters.*

@Singleton
class FirestoreTeamTokenRepository @Inject() (
    firestore: Firestore,
    blocking: Blocking.Service
) extends TeamTokenRepository {
  private val collection = firestore.collection("teams")

  private def teamTokenToMap(teamToken: TeamToken): ju.Map[String, AnyRef] = {
    Map(
      "teamId" -> teamToken.teamId,
      "teamName" -> teamToken.teamName,
      "scope" -> teamToken.scope,
      "botUserId" -> teamToken.botUserId,
      "botAccessToken" -> teamToken.botAccessToken,
      "timestamp" -> FieldValue.serverTimestamp()
    ).asJava
  }

  private def mapToTeamToken(map: ju.Map[String, AnyRef]): Option[TeamToken] = {
    val m = map.asScala
    (
      m.get("teamId"),
      m.get("teamName"),
      m.get("scope"),
      m.get("botUserId"),
      m.get("botAccessToken")
    ) match {
      case (
            Some(teamId: String),
            Some(teamName: String),
            Some(scope: String),
            Some(botUserId: String),
            Some(botAccessToken: String)
          ) =>
        Some(TeamToken(teamId, teamName, scope, botUserId, botAccessToken))
      case _ => None
    }
  }

  override def findBy(teamId: String): Task[Option[TeamToken]] =
    blocking.effectBlocking {
      val snapshot = collection.document(teamId).get().get()
      if (snapshot.exists()) {
        mapToTeamToken(snapshot.getData)
      } else {
        None
      }
    }

  override def store(teamToken: TeamToken): Task[Unit] =
    blocking.effectBlocking {
      val doc = collection.document(teamToken.teamId)
      doc.set(teamTokenToMap(teamToken)).get()
    }

}
