package glsf.format

import javax.inject.Inject

private[format] class MergedFormatter @Inject() (footerParser: FooterParser)
    extends MaybeFormatter {

  private val pat = """(?mi)^(Merge Request ![0-9]+ was merged)$""".r

  override def format(message: MailMessage): Option[SlackMessage] = {
    for {
      subject <- message.maybeSubject
      text <- message.maybeText
      bodyFooter <- footerParser.parse(text)
      m <- pat.findPrefixMatchOf(bodyFooter.body)
    } yield {
      val merged = m.group(1)
      val linkedSubject = Link(bodyFooter.url, subject).toMrkdwn
      SlackMessage.fromMrkdwn(s":sparkles: $linkedSubject\n$merged")
    }
  }
}
