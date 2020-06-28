package glsf.format

import com.slack.api.model.block.LayoutBlock

trait MaybeFormatter {
  def format(message: Message): Option[Seq[LayoutBlock]]
}
