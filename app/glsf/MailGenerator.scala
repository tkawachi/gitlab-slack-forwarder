package glsf

import zio.{UIO, ZIO}

import scala.util.Random

class MailGenerator(mailDomain: String) {
  def generate(): UIO[String] = ZIO.succeed {
    val localPart = Random.alphanumeric.take(16).mkString.toLowerCase
    s"a$localPart@$mailDomain"
  }
}
