package glsf.format

import com.google.inject.Guice
import org.scalatest.funsuite.AnyFunSuite

class MergedFormatterTest extends AnyFunSuite {
  private val injector = Guice.createInjector(new FormatterModule)

  Seq("merged1", "merged2").foreach { name =>
    test(s"format $name") {
      new FormatterGoldenTest {
        override def formatter: MaybeFormatter =
          injector.getInstance(classOf[MergedFormatter])

        override def testCase: String = name
      }
    }
  }
}
