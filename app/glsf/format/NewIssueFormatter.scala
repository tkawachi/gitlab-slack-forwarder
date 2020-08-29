package glsf.format

import javax.inject.Inject

private[format] class NewIssueFormatter @Inject() (footerParser: FooterParser)
    extends MaybeFormatter {
  private val pat =
    """^(.+ created an issue): http""".r

  override def format(message: MailMessage): Option[SlackMessage] = {
    for {
      subject <- message.maybeSubject
      text <- message.maybeText
      bodyFooter <- footerParser.parse(text)
      m <- pat.findPrefixMatchOf(bodyFooter.body)
    } yield {
      val line = m.group(1).strip()
      val linkedSubject = Link(bodyFooter.url, subject).toMrkdwn
      SlackMessage.fromMrkdwn(s":memo: $linkedSubject\n$line")
    }
  }
}
