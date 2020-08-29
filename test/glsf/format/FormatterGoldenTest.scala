package glsf.format

import com.slack.api.model.block.LayoutBlock
import com.slack.api.util.json.GsonFactory
import org.scalatest.Assertions
import play.api.libs.json.{JsValue, Json}

import scala.io.Source
import scala.jdk.CollectionConverters._

private[format] trait FormatterGoldenTest extends Assertions {
  def formatter: MaybeFormatter
  def testCase: String

  private def readDataParts(testCase: String): Map[String, Seq[String]] =
    Json
      .parse(Source.fromResource(s"$testCase.json").mkString)
      .as[Map[String, Seq[String]]]

  private def readBlocks(testCase: String): JsValue =
    (Json.parse(
      Source.fromResource(s"$testCase-block.json").mkString
    ) \ "blocks")
      .as[JsValue]

  private def blocksJson(blocks: Seq[LayoutBlock]): JsValue = {
    val s = GsonFactory.createSnakeCase().toJson(blocks.asJava)
    Json.parse(s)
  }

  private val message = Message(readDataParts(testCase))
  private val expected = readBlocks(testCase)

  private val blocks = formatter.format(message)
  assert(blocks.map(blocksJson).contains(expected))
}
