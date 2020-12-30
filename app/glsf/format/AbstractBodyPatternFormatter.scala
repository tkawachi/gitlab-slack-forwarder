package glsf.format

import scala.util.matching.Regex

private[format] abstract class AbstractBodyPatternFormatter(
    footerParser: FooterParser
) extends MaybeFormatter {

  /**
    * 本文に合致するパターン。
    */
  protected def bodyPattern: Regex

  /**
    * 通知の先頭に入れる slack 絵文字。
    */
  protected def emoji: String

  override def format(message: MailMessage): Option[SlackMessage] = {
    for {
      subject <- message.maybeSubject
      text <- message.maybeText
      bodyFooter <- footerParser.parse(text)
      m <- bodyPattern.findPrefixMatchOf(bodyFooter.body)
    } yield {
      val text = m.group(1).strip()
      val linkedSubject = Link(bodyFooter.url, subject).toMrkdwn
      SlackMessage.fromMrkdwn(s"$emoji $linkedSubject\n$text")
    }
  }
}
