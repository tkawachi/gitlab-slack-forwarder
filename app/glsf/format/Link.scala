package glsf.format

private[format] case class Link(url: String, title: String) {
  def toMrkdwn: String = s"<$url|$title>"
}
