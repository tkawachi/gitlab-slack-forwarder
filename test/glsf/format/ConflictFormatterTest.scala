package glsf.format

import com.google.inject.Guice
import org.scalatest.funsuite.AnyFunSuite

class ConflictFormatterTest extends AnyFunSuite {
  private val injector = Guice.createInjector(new FormatterModule)
  test("format conflict1") {
    new FormatterGoldenTest {
      override def formatter: MaybeFormatter =
        injector.getInstance(classOf[ConflictFormatter])
      override def testCase: String = "conflict1"
    }
  }
}
