package glsf.format

import com.slack.api.model.block.composition.MarkdownTextObject
import com.slack.api.model.block.{LayoutBlock, SectionBlock}

case class SlackMessage(mrkdwn: String, blocks: Seq[LayoutBlock])

object SlackMessage {

  def fromMrkdwn(mrkdwn: String): SlackMessage = {
    val maxLen = 3000
    val truncatedMrkdwn = mrkdwn.take(maxLen)
    SlackMessage(
      truncatedMrkdwn,
      Seq(
        SectionBlock
          .builder()
          .text(
            MarkdownTextObject
              .builder()
              .text(truncatedMrkdwn)
              .build()
          )
          .build()
      )
    )
  }
}
