package glsf.format

import com.google.inject.Guice
import org.scalatest.FunSuite

class PipelineFailedFormatterTest extends FunSuite {
  private val injector = Guice.createInjector(new FormatterModule)

  Seq("pipeline-failed1").foreach { name =>
    test(s"format $name") {
      new FormatterGoldenTest {
        override def formatter: MaybeFormatter =
          injector.getInstance(classOf[PipelineFailedFormatter])

        override def testCase: String = name
      }
    }
  }
}
