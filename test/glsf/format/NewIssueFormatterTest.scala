package glsf.format

import com.google.inject.Guice
import org.scalatest.funsuite.AnyFunSuite

class NewIssueFormatterTest extends AnyFunSuite {
  private val injector = Guice.createInjector(new FormatterModule)

  test("format new-issue1") {
    new FormatterGoldenTest {
      override def formatter: MaybeFormatter =
        injector.getInstance(classOf[NewIssueFormatter])
      override def testCase: String = "new-issue1"
    }
  }
}
