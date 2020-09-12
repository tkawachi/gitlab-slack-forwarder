package glsf.format

import javax.inject.Inject

private[format] class AddedAsApproverFormatter @Inject() (
    footerParser: FooterParser
) extends MaybeFormatter {

  private val pat = """(?m)^.* added you as an approver for !\d+$""".r

  override def format(message: MailMessage): Option[SlackMessage] = {
    for {
      subject <- message.maybeSubject
      text <- message.maybeText
      bodyFooter <- footerParser.parse(text)
      m <- pat.findFirstMatchIn(bodyFooter.body)
    } yield {
      val linkedSubject = Link(bodyFooter.url, subject).toMrkdwn
      SlackMessage.fromMrkdwn(
        s":eyes: $linkedSubject\nYou're added as an approver."
      )
    }
  }
}
