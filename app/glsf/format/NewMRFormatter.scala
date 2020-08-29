package glsf.format

import com.slack.api.model.block.composition.MarkdownTextObject
import com.slack.api.model.block.{LayoutBlock, SectionBlock}
import javax.inject.Inject

private[format] class NewMRFormatter @Inject() (footerParser: FooterParser)
    extends MaybeFormatter {
  private val pat =
    """^(.+ created a merge request): http""".r
  private val assigneePat = """(?m)^(Assignee: .+)""".r

  override def format(message: Message): Option[Seq[LayoutBlock]] = {
    for {
      subject <- message.maybeSubject
      text <- message.maybeText
      bodyFooter <- footerParser.parse(text)
      m <- pat.findPrefixMatchOf(bodyFooter.body)
    } yield {
      val line = m.group(1).strip()
      val linkedSubject = Link(bodyFooter.url, subject).toMrkdwn
      val assigneeMatch = assigneePat.findFirstMatchIn(bodyFooter.body)
      val body = Seq(
        Some(s":nerd_face: $linkedSubject"),
        Some(line),
        assigneeMatch.map(_.group(1))
      ).flatten.mkString("\n")

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
}
