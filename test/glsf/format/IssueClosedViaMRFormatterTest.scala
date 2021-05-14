package glsf.format

import com.google.inject.Guice
import org.scalatest.funsuite.AnyFunSuite

class IssueClosedViaMRFormatterTest extends AnyFunSuite {
  private val injector = Guice.createInjector(new FormatterModule)

  Seq("issue-closed-via-mr1").foreach { name =>
    test(s"format $name") {
      new FormatterGoldenTest {
        override def formatter: MaybeFormatter =
          injector.getInstance(classOf[IssueClosedViaMRFormatter])

        override def testCase: String = name
      }
    }
  }
}
