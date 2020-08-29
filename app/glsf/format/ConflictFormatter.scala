package glsf.format

import javax.inject.Inject

private[format] class ConflictFormatter @Inject() (footerParser: FooterParser)
    extends MaybeFormatter {
  private val pat = """^.+can no longer be merged due to conflict.""".r

  override def format(message: MailMessage): Option[SlackMessage] = {
    for {
      subject <- message.maybeSubject
      text <- message.maybeText
      bodyFooter <- footerParser.parse(text)
      m <- pat.findPrefixMatchOf(bodyFooter.body)
    } yield {
      val linkedSubject = Link(bodyFooter.url, subject).toMrkdwn
      SlackMessage.fromMrkdwn(s":skull: $linkedSubject\nConflicted.")
    }
  }

}
