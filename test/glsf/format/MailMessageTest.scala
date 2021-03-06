package glsf.format

import org.scalatest.funsuite.AnyFunSuite

class MailMessageTest extends AnyFunSuite {
  test("escape") {
    val text =
      MailMessage(Map("foo" -> Seq("<><>fo\n>\r\n>\n<o&&bar;+")))
        .maybeSingle("foo")
    assert(
      text.contains("&lt;&gt;&lt;&gt;fo\n&gt;\n&gt;\n&lt;o&amp;&amp;bar;+")
    )
  }
}
