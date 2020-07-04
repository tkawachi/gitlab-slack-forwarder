package glsf.format

import com.google.inject.Guice
import org.scalatest.FunSuite

class NewMRFormatterTest extends FunSuite {
  private val injector = Guice.createInjector(new FormatterModule)

  Seq("new-mr1").foreach { name =>
    test(s"format $name") {
      new FormatterGoldenTest {
        override def formatter: MaybeFormatter =
          injector.getInstance(classOf[NewMRFormatter])

        override def testCase: String = name
      }
    }
  }
}
