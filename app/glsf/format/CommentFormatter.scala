package glsf.format
import com.slack.api.model.block.composition.MarkdownTextObject
import com.slack.api.model.block.{LayoutBlock, SectionBlock}
import javax.inject.Inject

private[format] class CommentFormatter @Inject()(footerParser: FooterParser)
    extends MaybeFormatter {

  private val pat =
    """^(.+) (?:commented|started a new discussion).+(?s)(.+)$""".r

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
          .strip()
      val linkedSubject = Link(bodyFooter.url, subject).toMrkdwn
      Seq(
        SectionBlock
          .builder()
          .text(
            MarkdownTextObject
              .builder()
              .text(s":bookmark_tabs: $linkedSubject\n$who:\n$rest")
              .build()
          )
          .build()
      )
    }
  }
}
