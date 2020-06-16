package glsf

import scala.util.Random

class MailGenerator(mailDomain: String) {
  def generate(): String = {
    val localPart = Random.alphanumeric.take(16).mkString.toLowerCase
    s"a$localPart@$mailDomain"
  }
}
