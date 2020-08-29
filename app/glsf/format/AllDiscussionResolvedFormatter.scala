package glsf.format

import javax.inject.Inject

private[format] class AllDiscussionResolvedFormatter @Inject() (
    footerParser: FooterParser
) extends MaybeFormatter {

  private val pat = """^(All discussions on .+ were resolved by .+)""".r

  override def format(message: MailMessage): Option[SlackMessage] = {
    for {
      subject <- message.maybeSubject
      text <- message.maybeText
      bodyFooter <- footerParser.parse(text)
      m <- pat.findPrefixMatchOf(bodyFooter.body)
    } yield {
      val line = m.group(1).strip()
      val linkedSubject = Link(bodyFooter.url, subject).toMrkdwn
      SlackMessage.fromMrkdwn(s":heavy_check_mark: $linkedSubject\n$line")
    }
  }
}
