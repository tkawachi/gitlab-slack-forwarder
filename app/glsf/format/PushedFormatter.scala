package glsf.format

import com.slack.api.model.block.{LayoutBlock, SectionBlock}
import com.slack.api.model.block.composition.MarkdownTextObject
import javax.inject.Inject

private[format] class PushedFormatter @Inject() (footerParser: FooterParser)
    extends MaybeFormatter {
  private val pat =
    """(?m)^(.+ pushed new commits to merge request ![0-9]+)$""".r

  override def format(message: Message): Option[Seq[LayoutBlock]] = {
    for {
      subject <- message.maybeSubject
      text <- message.maybeText
      bodyFooter <- footerParser.parse(text)
      m <- pat.findPrefixMatchOf(bodyFooter.body)
    } yield {
      val message = m.group(1).strip()
      val linkedSubject = Link(bodyFooter.url, subject).toMrkdwn
      Seq(
        SectionBlock
          .builder()
          .text(
            MarkdownTextObject
              .builder()
              .text(s":muscle: $linkedSubject\n$message")
              .build()
          )
          .build()
      )
    }
  }
}
