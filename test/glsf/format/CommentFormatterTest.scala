package glsf.format

import com.google.inject.Guice
import org.scalatest.funsuite.AnyFunSuite

class CommentFormatterTest extends AnyFunSuite {
  private val injector = Guice.createInjector(new FormatterModule)

  Seq("comment1", "comment2").foreach { name =>
    test(s"format $name") {
      new FormatterGoldenTest {
        override def formatter: MaybeFormatter =
          injector.getInstance(classOf[CommentFormatter])

        override def testCase: String = name
      }
    }
  }
}
