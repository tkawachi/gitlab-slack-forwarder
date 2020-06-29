package glsf.format

import com.google.inject.Guice
import org.scalatest.FunSuite

class ReviewFormatterTest extends FunSuite {
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
