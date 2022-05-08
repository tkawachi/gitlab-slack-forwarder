package glsf.format

import org.scalatest.funsuite.AnyFunSuite

class FooterParserTest extends AnyFunSuite {
  test("parse") {
    val text =
      "\n\nMerge request !123 was merged\nMerge request URL: https://gitlab.example.com/GROUP1/project1/-/merge_requests/123\nBranches: branch-123 to master\nAuthor: User1\nAssignees: \nReviewer: User2\n\n-- \nView it on GitLab: https://gitlab.example.com/GROUP1/project1/-/merge_requests/123\nYou're receiving this email because of your account on gitlab.example.com.\n\n\n"

    val maybeBodyFooter = new FooterParser()
      .parse(text)
    assert(
      maybeBodyFooter
        .map(_.body)
        .contains(
          "Merge request !123 was merged\nMerge request URL: https://gitlab.example.com/GROUP1/project1/-/merge_requests/123\nBranches: branch-123 to master\nAuthor: User1\nAssignees: \nReviewer: User2"
        )
    )
    assert(
      maybeBodyFooter
        .map(_.url)
        .contains(
          "https://gitlab.example.com/GROUP1/project1/-/merge_requests/123"
        )
    )

  }

}
