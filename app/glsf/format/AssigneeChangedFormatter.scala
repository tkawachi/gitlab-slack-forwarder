package glsf.format

import javax.inject.Inject

private[format] class AssigneeChangedFormatter @Inject() (
    footerParser: FooterParser
) extends MaybeFormatter {

  private val pat = """(?m)^(Assignee changed .* to .*)$""".r

  override def format(message: MailMessage): Option[SlackMessage] = {
    for {
      subject <- message.maybeSubject
      text <- message.maybeText
      bodyFooter <- footerParser.parse(text)
      m <- pat.findFirstMatchIn(bodyFooter.body)
    } yield {
      val line = m.group(1).strip()
      val linkedSubject = Link(bodyFooter.url, subject).toMrkdwn
      SlackMessage.fromMrkdwn(s":frog: $linkedSubject\n$line")
    }
  }
}
