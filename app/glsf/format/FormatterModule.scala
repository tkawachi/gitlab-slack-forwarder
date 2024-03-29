package glsf.format

import com.google.inject.{AbstractModule, Provides}

import javax.inject.Singleton

class FormatterModule extends AbstractModule {
  override def configure(): Unit = {}

  @Singleton
  @Provides
  def formatters(
      commentFormatter: CommentFormatter,
      reviewFormatter: ReviewFormatter,
      firstLineFormatter: FirstLineFormatter
  ): Seq[MaybeFormatter] =
    Seq(
      commentFormatter,
      reviewFormatter,
      firstLineFormatter
    )
}
