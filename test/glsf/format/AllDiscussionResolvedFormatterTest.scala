package glsf.format

import com.google.inject.Guice
import org.scalatest.FunSuite

class AllDiscussionResolvedFormatterTest extends FunSuite {
  private val injector = Guice.createInjector(new FormatterModule)

  Seq("all-discussion-resolved1").foreach { name =>
    test(s"format $name") {
      new FormatterGoldenTest {
        override def formatter: MaybeFormatter =
          injector.getInstance(classOf[AllDiscussionResolvedFormatter])

        override def testCase: String = name
      }
    }
  }
}
