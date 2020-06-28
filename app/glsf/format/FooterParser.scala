package glsf.format

import javax.inject.Singleton

private[format] class FooterParser {
  import FooterParser._

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
    """(?s)^(.+)-- \nView it on GitLab: (http[^\s]+)\nYou're receiving this email because of your account on ([^\s]+)\.""".r
}
