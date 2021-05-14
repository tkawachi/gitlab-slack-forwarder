package glsf.format

import com.google.inject.Guice
import org.scalatest.funsuite.AnyFunSuite

class IssueClosedFormatterTest extends AnyFunSuite {
  private val injector = Guice.createInjector(new FormatterModule)

  Seq("issue-closed1", "mr-closed1").foreach { name =>
    test(s"format $name") {
      new FormatterGoldenTest {
        override def formatter: MaybeFormatter =
          injector.getInstance(classOf[IssueClosedFormatter])

        override def testCase: String = name
      }
    }
  }
}
