package glsf.format

import com.google.inject.Guice
import org.scalatest.FunSuite

class ApproveFormatterTest extends FunSuite {
  private val injector = Guice.createInjector(new FormatterModule)

  Seq("approve1").foreach { name =>
    test(s"format $name") {
      new FormatterGoldenTest {
        override def formatter: MaybeFormatter =
          injector.getInstance(classOf[ApproveFormatter])

        override def testCase: String = name
      }
    }
  }
}