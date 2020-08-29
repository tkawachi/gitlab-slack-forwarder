package glsf.format

import com.slack.api.model.block.composition.MarkdownTextObject
import com.slack.api.model.block.{LayoutBlock, SectionBlock}
import javax.inject.Inject

private[format] class ReviewFormatter @Inject() (footerParser: FooterParser)
    extends MaybeFormatter {

  private val pat =
    """^Merge request http.+ was reviewed by (.+)(?s)(.+)""".r

  override def format(message: Message): Option[Seq[LayoutBlock]] = {
    for {
      subject <- message.maybeSubject
      text <- message.maybeText
      bodyFooter <- footerParser.parse(text)
      m <- pat.findPrefixMatchOf(bodyFooter.body)
    } yield {
      val who = m.group(1).strip()
      val rest =
        m.group(2)
          .replaceAll("(?m)^&gt;.*$", "")
          .replaceAll("\n+", "\n")
          .replaceAll("(?m)^--\n", "")
          .replaceAll("(?m)^.+ started a new discussion on .+$", "")
          .strip()
      val linkedSubject = Link(bodyFooter.url, subject).toMrkdwn
      Seq(
        SectionBlock
          .builder()
          .text(
            MarkdownTextObject
              .builder()
              .text(s":speech_balloon: $linkedSubject\n$who:\n$rest")
              .build()
          )
          .build()
      )
    }
  }
}
