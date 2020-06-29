package glsf.format

case class Message(dataParts: Map[String, Seq[String]]) {
  def maybeSingle(key: String): Option[String] =
    dataParts
      .get(key)
      .flatMap(_.headOption)
      .map(
        _.replaceAll("\r\n", "\n")
          .replaceAll("&", "&amp;")
          .replaceAll("<", "&lt;")
          .replaceAll(">", "&gt;")
      )

  lazy val maybeSubject: Option[String] = maybeSingle("subject")
  lazy val maybeText: Option[String] = maybeSingle("text")
}
