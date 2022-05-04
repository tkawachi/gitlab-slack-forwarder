package glsf

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.firebase.cloud.FirestoreClient
import com.google.firebase.{FirebaseApp, FirebaseOptions}
import com.google.inject.{AbstractModule, Provides}
import com.typesafe.scalalogging.StrictLogging

import javax.inject.Singleton

class FirebaseModule extends AbstractModule with StrictLogging {
  override def configure(): Unit = {
    bind(classOf[UserRepository]).to(classOf[FirestoreUserRepository])
    bind(classOf[TeamTokenRepository]).to(classOf[FirestoreTeamTokenRepository])
    bind(classOf[DebugDataSaver]).to(classOf[FirestoreDebugDataSaver])
  }

  @Singleton
  @Provides
  def firestore(appConfig: AppConfig): Firestore = {
    logger.info("Initializing firebase")
    val credentials = GoogleCredentials.getApplicationDefault
    val options = FirebaseOptions
      .builder()
      .setCredentials(credentials)
      .setProjectId(appConfig.gcpProjectId)
      .build()
    try {
      FirebaseApp.initializeApp(options)
    } catch {
      case e: IllegalStateException =>
        logger.debug("FirebaseApp is already initialized", e)
    }
    FirestoreClient.getFirestore
  }
}
