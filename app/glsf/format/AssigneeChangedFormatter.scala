package glsf.format

import com.slack.api.model.block.composition.MarkdownTextObject
import com.slack.api.model.block.{LayoutBlock, SectionBlock}
import javax.inject.Inject

private[format] class AssigneeChangedFormatter @Inject()(
  footerParser: FooterParser
) extends MaybeFormatter {

  private val pat = """(?m)^(Assignee changed .* to .*)$""".r

  override def format(message: Message): Option[Seq[LayoutBlock]] = {
    for {
      subject <- message.maybeSubject
      text <- message.maybeText
      bodyFooter <- footerParser.parse(text)
      m <- pat.findFirstMatchIn(bodyFooter.body)
    } yield {
      val line = m.group(1).strip()
      val linkedSubject = Link(bodyFooter.url, subject).toMrkdwn
      Seq(
        SectionBlock
          .builder()
          .text(
            MarkdownTextObject
              .builder()
              .text(s":frog: $linkedSubject\n$line")
              .build()
          )
          .build()
      )
    }
  }
}
