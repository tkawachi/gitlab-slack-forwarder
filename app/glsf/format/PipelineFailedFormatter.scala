package glsf.format

import javax.inject.Inject

private[format] class PipelineFailedFormatter @Inject() ()
    extends MaybeFormatter {

  private val pat =
    """(?ms)^Your pipeline has failed\.$.+Pipeline #[0-9]+ \( (http[^\s]+) \)""".r

  override def format(message: MailMessage): Option[SlackMessage] = {
    for {
      subject <- message.maybeSubject
      text <- message.maybeText
      m <- pat.findPrefixMatchOf(text.strip())
    } yield {
      val url = m.group(1)
      val linkedSubject = Link(url, subject).toMrkdwn
      SlackMessage.fromMrkdwn(s":boom: $linkedSubject")
    }
  }
}
