package glsf.format

trait MaybeFormatter {
  def format(message: MailMessage): Option[SlackMessage]
}
