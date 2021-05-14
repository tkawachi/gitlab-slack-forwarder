package glsf.format

import com.google.inject.Guice
import org.scalatest.funsuite.AnyFunSuite

class MessageFormatterTest extends AnyFunSuite {

  private val injector = Guice.createInjector(new FormatterModule)

  private val testCases =
    Seq(
      "added-as-approver1",
      "all-discussion-resolved1",
      "approve1",
      "assignee-changed1",
      "comment1",
      "comment2",
      "issue-closed1",
      "issue-closed-via-mr1",
      "merged1",
      "mr-closed1",
      "new-issue1",
      "new-mr1",
      "pipeline-failed1",
      "pipeline-fixed1",
      "pushed1",
      "conflict1",
      "review1",
      "reviewer-changed1",
      "mr-scheduled-to-merge1"
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
