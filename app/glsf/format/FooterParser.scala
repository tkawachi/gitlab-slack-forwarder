package glsf.format

private[format] class FooterParser {
  import FooterParser.*

  def parse(text: String): Option[BodyFooter] = {
    footerPat.findPrefixMatchOf(text).map { m =>
      val body = m.group(1).strip()
      val url = m.group(2)
      BodyFooter(body, url)
    }
  }
}

object FooterParser {
  private val footerPat =
    """(?s)^(.+)-- \nView it on GitLab: (http[^\s]+)\nYou're receiving this email because""".r
}
