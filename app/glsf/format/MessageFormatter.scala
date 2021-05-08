package glsf.format

import javax.inject.Inject

class MessageFormatter @Inject() (formatters: Seq[MaybeFormatter])
    extends MaybeFormatter {

  override def format(message: MailMessage): Option[SlackMessage] =
    formatters
      .foldLeft(None: Option[SlackMessage])((maybeBlocks, formatter) =>
        maybeBlocks.orElse(formatter.format(message))
      )

  def formatOrDefault(message: MailMessage): SlackMessage =
    format(message).getOrElse(defaultFallback(message))

  /** Default fallback for unknown format mail.
    */
  def defaultFallback(message: MailMessage): SlackMessage = {
    val body =
      Seq(message.maybeSubject, message.maybeText).flatten.mkString("\n")
    SlackMessage.fromMrkdwn(body)
  }

}
