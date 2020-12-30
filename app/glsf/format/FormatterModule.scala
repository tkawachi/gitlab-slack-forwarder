package glsf.format

import com.google.inject.{AbstractModule, Provides}
import javax.inject.Singleton

class FormatterModule extends AbstractModule {
  override def configure(): Unit = {}

  @Singleton
  @Provides
  def formatters(
      addedAsApproverFormatter: AddedAsApproverFormatter,
      allDiscussionResolvedFormatter: AllDiscussionResolvedFormatter,
      assigneeChangedFormatter: AssigneeChangedFormatter,
      approveFormatter: ApproveFormatter,
      commentFormatter: CommentFormatter,
      issueClosedViaMRFormatter: IssueClosedViaMRFormatter,
      issueClosedFormatter: IssueClosedFormatter,
      mergedFormatter: MergedFormatter,
      newIssueFormatter: NewIssueFormatter,
      newMRFormatter: NewMRFormatter,
      pipelineFailedFormatter: PipelineFailedFormatter,
      pipelineFixedFormatter: PipelineFixedFormatter,
      pushedFormatter: PushedFormatter,
      conflictFormatter: ConflictFormatter,
      reviewFormatter: ReviewFormatter,
      scheduledToMergeFormatter: ScheduledToMergeFormatter
  ): Seq[MaybeFormatter] =
    Seq(
      addedAsApproverFormatter,
      allDiscussionResolvedFormatter,
      assigneeChangedFormatter,
      approveFormatter,
      commentFormatter,
      issueClosedViaMRFormatter,
      issueClosedFormatter,
      mergedFormatter,
      newIssueFormatter,
      newMRFormatter,
      pipelineFailedFormatter,
      pipelineFixedFormatter,
      pushedFormatter,
      conflictFormatter,
      reviewFormatter,
      scheduledToMergeFormatter
    )
}
