package glsf.format

import com.slack.api.model.block.composition.MarkdownTextObject
import com.slack.api.model.block.{LayoutBlock, SectionBlock}
import javax.inject.Inject

class MessageFormatter @Inject()(formatters: Seq[MaybeFormatter])
    extends MaybeFormatter {

  override def format(message: Message): Option[Seq[LayoutBlock]] =
    formatters
      .foldLeft(None: Option[Seq[LayoutBlock]])(
        (maybeBlocks, formatter) =>
          maybeBlocks.orElse(formatter.format(message))
      )

  def formatOrDefault(message: Message): Seq[LayoutBlock] =
    format(message).getOrElse(defaultFallback(message))

  /**
    * Default fallback for unknown format mail.
    */
  def defaultFallback(message: Message): Seq[LayoutBlock] = {
    val body =
      Seq(message.maybeSubject, message.maybeText).flatten.mkString("\n")
    Seq(
      SectionBlock
        .builder()
        .text(
          MarkdownTextObject
            .builder()
            .text(body)
            .build()
        )
        .build()
    )
  }

}
