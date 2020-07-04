package glsf.format

import com.google.inject.{AbstractModule, Provides}
import javax.inject.Singleton

class FormatterModule extends AbstractModule {
  override def configure(): Unit = {}

  @Singleton
  @Provides
  def formatters(assigneeChangedFormatter: AssigneeChangedFormatter,
                 approveFormatter: ApproveFormatter,
                 commentFormatter: CommentFormatter,
                 issueClosedViaMRFormatter: IssueClosedViaMRFormatter,
                 issueClosedFormatter: IssueClosedFormatter,
                 mergedFormatter: MergedFormatter,
                 newIssueFormatter: NewIssueFormatter,
                 pipelineFailedFormatter: PipelineFailedFormatter,
                 pushedFormatter: PushedFormatter,
                 conflictFormatter: ConflictFormatter,
                 reviewFormatter: ReviewFormatter): Seq[MaybeFormatter] =
    Seq(
      assigneeChangedFormatter,
      approveFormatter,
      commentFormatter,
      issueClosedViaMRFormatter,
      issueClosedFormatter,
      mergedFormatter,
      newIssueFormatter,
      pipelineFailedFormatter,
      pushedFormatter,
      conflictFormatter,
      reviewFormatter
    )
}
