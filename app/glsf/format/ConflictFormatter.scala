package glsf.format

import com.slack.api.model.block.{LayoutBlock, SectionBlock}
import com.slack.api.model.block.composition.MarkdownTextObject
import javax.inject.Inject

class ConflictFormatter @Inject()(footerParser: FooterParser)
    extends MaybeFormatter {
  private val pat = """^.+can no longer be merged due to conflict.""".r

  override def format(message: Message): Option[Seq[LayoutBlock]] = {
    for {
      subject <- message.maybeSubject
      text <- message.maybeText
      bodyFooter <- footerParser.parse(text)
      m <- pat.findPrefixMatchOf(bodyFooter.body)
    } yield {
      val linkedSubject = Link(bodyFooter.url, subject).toMrkdwn
      Seq(
        SectionBlock
          .builder()
          .text(
            MarkdownTextObject
              .builder()
              .text(s":boom: $linkedSubject\nConflicted.")
              .build()
          )
          .build()
      )
    }
  }

}
