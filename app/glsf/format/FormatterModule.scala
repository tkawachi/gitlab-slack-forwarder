package glsf.format

import com.google.inject.{AbstractModule, Provides}

class FormatterModule extends AbstractModule {
  override def configure(): Unit = {}

  @Provides
  def formatters(commentFormatter: CommentFormatter,
                 issueClosedFormatter: IssueClosedViaMRFormatter,
                 mergedFormatter: MergedFormatter,
                 pushedFormatter: PushedFormatter): Seq[MaybeFormatter] =
    Seq(
      commentFormatter,
      issueClosedFormatter,
      mergedFormatter,
      pushedFormatter
    )
}
