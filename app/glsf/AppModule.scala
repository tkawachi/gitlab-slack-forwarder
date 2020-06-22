package glsf

import java.util.concurrent.Executors

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.firebase.cloud.FirestoreClient
import com.google.firebase.{FirebaseApp, FirebaseOptions}
import com.google.inject.{AbstractModule, Provides}
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import javax.inject.{Named, Singleton}
import play.api.inject.ApplicationLifecycle
import pureconfig.ConfigSource
import pureconfig.generic.auto._

import scala.concurrent.{ExecutionContext, Future}

class AppModule extends AbstractModule with LazyLogging {
  override def configure(): Unit = {
//    bind(classOf[UserRepository]).to(classOf[MockUserRepository])
    bind(classOf[UserRepository]).to(classOf[FirestoreUserRepository])
//    bind(classOf[TeamTokenRepository]).to(classOf[MockTeamTokenRepository])
    bind(classOf[TeamTokenRepository]).to(classOf[FirestoreTeamTokenRepository])
    bind(classOf[DebugDataSaver]).to(classOf[FirestoreDebugDataSaver])
  }

  @Provides
  def appConfig(config: Config): AppConfig = {
    ConfigSource.fromConfig(config).at("glsf").load[AppConfig] match {
      case Right(c)  => c
      case Left(err) => sys.error(err.prettyPrint())
    }
  }

  @Provides
  def slackConfig(config: Config): SlackConfig =
    ConfigSource.fromConfig(config).at("glsf.slack").load[SlackConfig] match {
      case Right(c)  => c
      case Left(err) => sys.error(err.prettyPrint())
    }

  @Provides
  def mailGenerator(appConfig: AppConfig): MailGenerator =
    new MailGenerator(appConfig.mailDomain)

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
    FirebaseApp.initializeApp(options)
    FirestoreClient.getFirestore
  }

  @Singleton
  @Named("io")
  @Provides
  def ioExecutionContext(
    applicationLifecycle: ApplicationLifecycle
  ): ExecutionContext = {
    logger.info("Initializing I/O execution context")
    val executors = Executors.newCachedThreadPool()
    applicationLifecycle.addStopHook(
      () => Future.successful(executors.shutdown())
    )
    ExecutionContext.fromExecutorService(executors)
  }
}
