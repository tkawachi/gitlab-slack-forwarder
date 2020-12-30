package glsf.format

import javax.inject.Inject

private[format] class ScheduledToMergeFormatter @Inject() (
    footerParser: FooterParser
) extends MaybeFormatter {
  private val pat =
    """^(Merge Request !\d+ was scheduled to merge after pipeline succeeds by .+)""".r

  override def format(message: MailMessage): Option[SlackMessage] = {
    for {
      subject <- message.maybeSubject
      text <- message.maybeText
      bodyFooter <- footerParser.parse(text)
      m <- pat.findPrefixMatchOf(bodyFooter.body)
    } yield {
      val text = m.group(1).strip()
      val linkedSubject = Link(bodyFooter.url, subject).toMrkdwn
      SlackMessage.fromMrkdwn(s":stopwatch: $linkedSubject\n$text")
    }
  }
}
