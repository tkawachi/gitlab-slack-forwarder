package glsf.format

import com.slack.api.model.block.composition.MarkdownTextObject
import com.slack.api.model.block.{
  ContextBlock,
  ContextBlockElement,
  SectionBlock
}

import scala.jdk.CollectionConverters.*

/** 本文最初の行を取り出す。
  */
class FirstLineFormatter extends MaybeFormatter {
  override def format(message: MailMessage): Option[SlackMessage] = {
    val subject = message.maybeSubject.getOrElse("")
    val text = message.maybeText.getOrElse("")
    val urls = URLs.extractFromText(text)
    Option.when(urls.enough) {
      val icon = iconOf(text)
      val firstLine = text.linesIterator.toList.find(_.nonEmpty).getOrElse("")
      val mrkdwn = icon.map(_ + " " + firstLine).getOrElse(firstLine)

      SlackMessage(
        mrkdwn,
        Seq(
          SectionBlock
            .builder()
            .text(
              MarkdownTextObject
                .builder()
                .text(mrkdwn)
                .build()
            )
            .build(),
          ContextBlock
            .builder()
            .elements(
              Seq[ContextBlockElement](
                MarkdownTextObject
                  .builder()
                  .text(urls.asMrkdwn)
                  .build(),
                MarkdownTextObject
                  .builder()
                  .text(subject)
                  .build()
              ).asJava
            )
            .build()
        )
      )
    }
  }

  private val iconMap = Seq(
    "was scheduled to merge after pipeline succeeds" -> ":stopwatch:",
    "were resolved by" -> ":heavy_check_mark:",
    "was approved by" -> ":+1:",
    "due to conflict" -> ":skull:",
    "was closed by" -> ":package:",
    "was merged" -> ":sparkles:",
    "created an issue" -> ":memo:",
    "created a merge request" -> ":nerd_face:",
    "pushed new commits" -> ":muscle:",
    "was reviewed by" -> ":speech_balloon:",
    "has failed" -> ":boom:",
    "has been fixed" -> ":wink:",
    "as an approver for" -> ":eyes:"
  )

  def iconOf(text: String): Option[String] =
    iconMap.collectFirst {
      case (pat, s) if text.toLowerCase.contains(pat) => s
    }
}
