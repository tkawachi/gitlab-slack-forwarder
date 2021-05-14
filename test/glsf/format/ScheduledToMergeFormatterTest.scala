package glsf.format

import com.google.inject.Guice
import org.scalatest.funsuite.AnyFunSuite

class ScheduledToMergeFormatterTest extends AnyFunSuite {
  private val injector = Guice.createInjector(new FormatterModule)

  Seq("mr-scheduled-to-merge1").foreach { name =>
    test(s"format $name") {
      new FormatterGoldenTest {
        override def formatter: MaybeFormatter =
          injector.getInstance(classOf[ScheduledToMergeFormatter])

        override def testCase: String = name
      }
    }
  }
}
