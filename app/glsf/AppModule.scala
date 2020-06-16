package glsf

import com.google.inject.{AbstractModule, Provides}
import com.typesafe.config.Config
import pureconfig.ConfigSource
import pureconfig.generic.auto._

class AppModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[UserRepository]).to(classOf[MockUserRepository])
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
