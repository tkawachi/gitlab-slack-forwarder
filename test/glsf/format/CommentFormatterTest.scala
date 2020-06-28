package glsf.format

import com.google.inject.Guice
import org.scalatest.FunSuite

class CommentFormatterTest extends FunSuite {
  private val injector = Guice.createInjector(new FormatterModule)

  test("format comment1") {
    new FormatterGoldenTest {
      override def formatter: MaybeFormatter =
        injector.getInstance(classOf[CommentFormatter])
      override def testCase: String = "comment1"
    }
  }
}
