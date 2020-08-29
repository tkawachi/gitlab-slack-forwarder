package glsf.format

import org.scalatest.FunSuite

class MailMessageTest extends FunSuite {
  test("escape") {
    val text =
      MailMessage(Map("foo" -> Seq("<><>fo\n>\r\n>\n<o&&bar;+")))
        .maybeSingle("foo")
    assert(
      text.contains("&lt;&gt;&lt;&gt;fo\n&gt;\n&gt;\n&lt;o&amp;&amp;bar;+")
    )
  }
}
