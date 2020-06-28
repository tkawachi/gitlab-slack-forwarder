package glsf.format

import com.slack.api.model.block.composition.MarkdownTextObject
import com.slack.api.model.block.{LayoutBlock, SectionBlock}
import javax.inject.Inject

private[format] class MergedFormatter @Inject()(footerParser: FooterParser)
    extends MaybeFormatter {

  private val pat = """(?m)^(Merge Request ![0-9]+ was merged)$""".r

  override def format(message: Message): Option[Seq[LayoutBlock]] = {
    for {
      subject <- message.maybeSubject
      text <- message.maybeText
      bodyFooter <- footerParser.parse(text)
      m <- pat.findPrefixMatchOf(bodyFooter.body)
    } yield {
      val merged = m.group(1)
      val linkedSubject = Link(bodyFooter.url, subject).toMrkdwn
      Seq(
        SectionBlock
          .builder()
          .text(
            MarkdownTextObject
              .builder()
              .text(s":sparkles: $linkedSubject\n$merged")
              .build()
          )
          .build()
      )
    }
  }
}
