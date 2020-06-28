package glsf.format

import com.google.inject.Guice
import org.scalatest.FunSuite

class PushedFormatterTest extends FunSuite {
  private val injector = Guice.createInjector(new FormatterModule)

  test("format pushed1") {
    new FormatterGoldenTest {
      override def formatter: MaybeFormatter =
        injector.getInstance(classOf[PushedFormatter])

      override def testCase: String = "pushed1"
    }
  }
}
