package glsf.format

import com.google.inject.Guice
import org.scalatest.FunSuite

class MergedFormatterTest extends FunSuite {
  private val injector = Guice.createInjector(new FormatterModule)
  test("format merged1") {
    new FormatterGoldenTest {
      override def formatter: MaybeFormatter =
        injector.getInstance(classOf[MergedFormatter])
      override def testCase: String = "merged1"
    }
  }
}
