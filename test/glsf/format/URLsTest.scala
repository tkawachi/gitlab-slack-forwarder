package glsf.format

import org.scalatest.funsuite.AnyFunSuite

class URLsTest extends AnyFunSuite {
  test("extract") {
    val text =
      "\n\nYour pipeline has failed.\n\nProject: foo ( https://example.com/foo/bar )\nBranch: sample-branch ( https://example.com/foo/bar/-/commits/sample-branch )\nMerge Request: !1234 ( https://example.com/foo/bar/-/merge_requests/1234 )\n\nCommit: 000000 ( https://example.com/foo/bar/-/commit/0000001f331de57d433608f2778471031eceec )\nCommit Message: Commit message here\n\nCommit Author: John Doe ( https://example.com/john.doe )\n\nPipeline #123456 ( https://example.com/foo/bar/-/pipelines/123456 ) triggered by John Doe ( https://example.com/john.doe )\nhad 1 failed build.\n\nJob #987654 ( https://example.com/foo/bar/-/jobs/987654/raw )\n\nStage: test\nName: test name\nTrace: trace here\n\n\n-- \nYou're receiving this email because of your account on example.com.\n\n\n\n"
    val expected = URLs(
      Some("https://example.com/foo/bar/-/jobs/987654/raw"),
      Some("https://example.com/foo/bar/-/pipelines/123456"),
      Some("https://example.com/foo/bar/-/merge_requests/1234"),
      None,
      Some(
        "https://example.com/foo/bar/-/commit/0000001f331de57d433608f2778471031eceec"
      ),
      Some("https://example.com/foo/bar/-/commits/sample-branch"),
      Set("https://example.com/foo/bar", "https://example.com/john.doe")
    )
    assert(URLs.extractFromText(text) == expected)
  }
}
