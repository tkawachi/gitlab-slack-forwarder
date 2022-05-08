package glsf.format

/** テキストから抜き出す各種URL
  */
case class URLs(
    job: Option[String],
    pipeline: Option[String],
    mergeRequest: Option[String],
    issue: Option[String],
    commit: Option[String],
    branch: Option[String],
    others: Set[String]
) {

  private def shortUrls = List(
    job.map(Link(_, "Job")),
    pipeline.map(Link(_, "Pipeline")),
    mergeRequest.map(Link(_, "MR")),
    issue.map(Link(_, "Issue")),
    commit.map(Link(_, "Commit")),
    branch.map(Link(_, "Branch"))
  ).flatten

  /** others を除くものを slack の mrkdwn 形式に。
    */
  def shortAsMrkdwn: String = shortUrls.map(_.toMrkdwn).mkString(" ")

  def asMrkdwn: String =
    (shortUrls ++ others.map { u =>
      val last = u.split('/').last
      val text = if (last.isEmpty) u else last
      Link(u, text)
    })
      .map(_.toMrkdwn)
      .mkString(" ")

  def enough: Boolean =
    job
      .orElse(pipeline)
      .orElse(mergeRequest)
      .orElse(issue)
      .orElse(commit)
      .orElse(branch)
      .isDefined
}

object URLs {
  private val pat =
    """https?://[\w_-]+(?:\.[\w_-]+)+[\w.,@?^=%&:/~+#-]*[\w@?^=%&/~+#-]""".r

  def extractFromText(text: String): URLs = {
    val allURLs = pat.findAllIn(text).toSet
    val job = allURLs.find(_.contains("/-/jobs/"))
    val pipeline = allURLs.find(_.contains("/-/pipelines/"))
    val mergeRequest = allURLs.find(_.contains("/-/merge_requests/"))
    val issue = allURLs.find(_.contains("/-/issues/"))
    val commit = allURLs.find(_.contains("/-/commit/"))
    val branch = allURLs.find(_.contains("/-/commits/"))
    val others =
      allURLs -- Set(job, pipeline, mergeRequest, issue, commit, branch).flatten
    URLs(
      job = job,
      pipeline = pipeline,
      mergeRequest = mergeRequest,
      issue = issue,
      commit = commit,
      branch = branch,
      others = others
    )
  }
}
