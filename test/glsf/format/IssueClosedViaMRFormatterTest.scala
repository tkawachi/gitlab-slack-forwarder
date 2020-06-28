package glsf.format

import com.google.inject.Guice
import org.scalatest.FunSuite

class IssueClosedViaMRFormatterTest extends FunSuite {
  private val injector = Guice.createInjector(new FormatterModule)

  test("format issue-closed-via-mr1") {
    new FormatterGoldenTest {
      override def formatter: MaybeFormatter =
        injector.getInstance(classOf[IssueClosedViaMRFormatter])
      override def testCase: String = "issue-closed-via-mr1"
    }
  }
}
