package glsf.format

import com.google.inject.Guice
import org.scalatest.funsuite.AnyFunSuite

class MessageFormatterTest extends AnyFunSuite {

  private val injector = Guice.createInjector(new FormatterModule)

  private val testCases =
    Seq(
      "comment1",
      "comment2",
      "review1"
    )

  testCases.foreach { tc =>
    test(s"format $tc") {
      new FormatterGoldenTest {
        override def formatter: MaybeFormatter =
          injector.getInstance(classOf[MessageFormatter])

        override def testCase: String = tc
      }
    }

  }

}
