package glsf.format

import javax.inject.Inject
import scala.util.matching.Regex

private[format] class IssueClosedFormatter @Inject() (
    footerParser: FooterParser
) extends AbstractBodyPatternFormatter(footerParser) {
  override protected val bodyPattern: Regex = """^(.+ was closed by .+)""".r
  override protected def emoji: String = ":package:"
}
