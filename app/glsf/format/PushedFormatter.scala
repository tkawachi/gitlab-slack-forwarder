package glsf.format

import javax.inject.Inject

private[format] class PushedFormatter @Inject() (footerParser: FooterParser)
    extends MaybeFormatter {
  private val pat =
    """(?m)^(.+ pushed new commits to merge request ![0-9]+)$""".r

  override def format(message: MailMessage): Option[SlackMessage] = {
    for {
      subject <- message.maybeSubject
      text <- message.maybeText
      bodyFooter <- footerParser.parse(text)
      m <- pat.findPrefixMatchOf(bodyFooter.body)
    } yield {
      val message = m.group(1).strip()
      val linkedSubject = Link(bodyFooter.url, subject).toMrkdwn
      SlackMessage.fromMrkdwn(s":muscle: $linkedSubject\n$message")
    }
  }
}
