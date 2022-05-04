package glsf

import com.google.inject.{AbstractModule, Provides, TypeLiteral}
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import pureconfig.ConfigSource
import pureconfig.generic.auto.*
import zio.blocking.Blocking
import zio.{Runtime, ZEnv}

class AppModule extends AbstractModule with LazyLogging {
  override def configure(): Unit = {
    bind(new TypeLiteral[Runtime[ZEnv]] {}).toInstance(Runtime.default)
    bind(classOf[Blocking.Service]).toInstance(Blocking.Service.live)
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

}
