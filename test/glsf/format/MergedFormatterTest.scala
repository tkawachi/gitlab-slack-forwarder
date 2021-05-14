package glsf.format

import com.google.inject.Guice
import org.scalatest.funsuite.AnyFunSuite

class MergedFormatterTest extends AnyFunSuite {
  private val injector = Guice.createInjector(new FormatterModule)
  test("format merged1") {
    new FormatterGoldenTest {
      override def formatter: MaybeFormatter =
        injector.getInstance(classOf[MergedFormatter])
      override def testCase: String = "merged1"
    }
  }
}
