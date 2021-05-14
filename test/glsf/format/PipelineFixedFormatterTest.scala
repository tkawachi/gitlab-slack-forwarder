package glsf.format

import com.google.inject.Guice
import org.scalatest.funsuite.AnyFunSuite

class PipelineFixedFormatterTest extends AnyFunSuite {
  private val injector = Guice.createInjector(new FormatterModule)

  Seq("pipeline-fixed1").foreach { name =>
    test(s"format $name") {
      new FormatterGoldenTest {
        override def formatter: MaybeFormatter =
          injector.getInstance(classOf[PipelineFixedFormatter])

        override def testCase: String = name
      }
    }
  }
}
