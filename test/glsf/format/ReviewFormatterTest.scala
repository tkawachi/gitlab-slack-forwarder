package glsf.format

import com.google.inject.Guice
import org.scalatest.funsuite.AnyFunSuite

class ReviewFormatterTest extends AnyFunSuite {
  private val injector = Guice.createInjector(new FormatterModule)

  Seq("review1").foreach { name =>
    test(s"format $name") {
      new FormatterGoldenTest {
        override def formatter: MaybeFormatter =
          injector.getInstance(classOf[ReviewFormatter])

        override def testCase: String = name
      }
    }
  }
}
