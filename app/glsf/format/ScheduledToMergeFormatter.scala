package glsf.format

import javax.inject.Inject
import scala.util.matching.Regex

private[format] class ScheduledToMergeFormatter @Inject() (
    footerParser: FooterParser
) extends AbstractBodyPatternFormatter(footerParser) {

  override protected def bodyPattern: Regex =
    """^(Merge Request !\d+ was scheduled to merge after pipeline succeeds by .+)""".r

  override protected def emoji: String = ":stopwatch:"
}
