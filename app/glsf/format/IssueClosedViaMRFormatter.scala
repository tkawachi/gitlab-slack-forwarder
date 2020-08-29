package glsf.format

import com.slack.api.model.block.composition.MarkdownTextObject
import com.slack.api.model.block.{LayoutBlock, SectionBlock}
import javax.inject.Inject

private[format] class IssueClosedViaMRFormatter @Inject() (
    footerParser: FooterParser
) extends MaybeFormatter {
  private val pat =
    """^(Issue was closed by .+ via merge request) (![0-9]+) \((.+)\)""".r

  override def format(message: Message): Option[Seq[LayoutBlock]] = {
    for {
      subject <- message.maybeSubject
      text <- message.maybeText
      bodyFooter <- footerParser.parse(text)
      m <- pat.findPrefixMatchOf(bodyFooter.body)
    } yield {
      val text = m.group(1).strip()
      val mrText = m.group(2)
      val mrUrl = m.group(3).strip()
      val mrLink = Link(mrUrl, mrText).toMrkdwn
      val linkedSubject = Link(bodyFooter.url, subject).toMrkdwn
      Seq(
        SectionBlock
          .builder()
          .text(
            MarkdownTextObject
              .builder()
              .text(s":package: $linkedSubject\n$text $mrLink")
              .build()
          )
          .build()
      )
    }
  }
}
