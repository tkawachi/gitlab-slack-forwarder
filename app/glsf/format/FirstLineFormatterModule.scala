package glsf.format

import com.google.inject.{AbstractModule, Provides}

import javax.inject.Singleton

class FirstLineFormatterModule extends AbstractModule {
  override def configure(): Unit = {}

  @Singleton
  @Provides
  def formatters(
      firstLineFormatter: FirstLineFormatter
  ): Seq[MaybeFormatter] =
    Seq(
      firstLineFormatter
    )
}
