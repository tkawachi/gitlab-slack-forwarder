package glsf.format

import com.google.inject.Guice
import org.scalatest.funsuite.AnyFunSuite

class AssigneeChangedFormatterTest extends AnyFunSuite {
  private val injector = Guice.createInjector(new FormatterModule)

  Seq("assignee-changed1", "reviewer-changed1").foreach { name =>
    test(s"format $name") {
      new FormatterGoldenTest {
        override def formatter: MaybeFormatter =
          injector.getInstance(classOf[AssigneeChangedFormatter])

        override def testCase: String = name
      }
    }
  }
}
