package glsf.format

import org.scalatest.FunSuite

class MessageTest extends FunSuite {
  test("escape") {
    val text =
      Message(Map("foo" -> Seq("<><>fo\n>\r\n>\n<o&&bar;+"))).maybeSingle("foo")
    assert(text.contains("&lt;&gt;&lt;&gt;fo\n>\n>\n&lt;o&amp;&amp;bar;+"))
  }
}
