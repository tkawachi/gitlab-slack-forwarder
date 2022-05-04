package glsf

import zio.UIO

import scala.util.Random

class MailGenerator(mailDomain: String) {
  def generate(): UIO[String] = UIO {
    val localPart = Random.alphanumeric.take(16).mkString.toLowerCase
    s"a$localPart@$mailDomain"
  }
}
