package glsf.format

import com.google.inject.{AbstractModule, Provides}

class FormatterModule extends AbstractModule {
  override def configure(): Unit = {}

  @Provides
  def formatters(approveFormatter: ApproveFormatter,
                 commentFormatter: CommentFormatter,
                 issueClosedFormatter: IssueClosedViaMRFormatter,
                 mergedFormatter: MergedFormatter,
                 pushedFormatter: PushedFormatter,
                 conflictFormatter: ConflictFormatter): Seq[MaybeFormatter] =
    Seq(
      approveFormatter,
      commentFormatter,
      issueClosedFormatter,
      mergedFormatter,
      pushedFormatter,
      conflictFormatter
    )
}
