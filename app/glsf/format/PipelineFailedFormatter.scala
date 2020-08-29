package glsf.format

import com.slack.api.model.block.composition.MarkdownTextObject
import com.slack.api.model.block.{LayoutBlock, SectionBlock}
import javax.inject.Inject

private[format] class PipelineFailedFormatter @Inject() ()
    extends MaybeFormatter {

  private val pat =
    """(?ms)^Your pipeline has failed\.$.+Pipeline #[0-9]+ \( (http[^\s]+) \)""".r

  override def format(message: Message): Option[Seq[LayoutBlock]] = {
    for {
      subject <- message.maybeSubject
      text <- message.maybeText
      m <- pat.findPrefixMatchOf(text.strip())
    } yield {
      val url = m.group(1)
      val linkedSubject = Link(url, subject).toMrkdwn
      Seq(
        SectionBlock
          .builder()
          .text(
            MarkdownTextObject
              .builder()
              .text(s":boom: $linkedSubject")
              .build()
          )
          .build()
      )
    }
  }
}
