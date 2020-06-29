package glsf.format

import com.google.inject.Guice
import org.scalatest.FunSuite

class MessageFormatterTest extends FunSuite {

  private val injector = Guice.createInjector(new FormatterModule)

  private val testCases =
    Seq(
      "approve1",
      "assignee-changed1",
      "comment1",
      "comment2",
      "issue-closed-via-mr1",
      "merged1",
      "new-issue1",
      "pipeline-failed1",
      "pushed1",
      "conflict1",
      "review1"
    )

  testCases.foreach { tc =>
    test(s"format $tc") {
      new FormatterGoldenTest {
        override def formatter: MaybeFormatter =
          injector.getInstance(classOf[MessageFormatter])
        override def testCase: String = tc
      }
    }

  }

}
