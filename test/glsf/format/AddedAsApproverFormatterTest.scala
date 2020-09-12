package glsf.format

import com.google.inject.Guice
import org.scalatest.FunSuite

class AddedAsApproverFormatterTest extends FunSuite {
  private val injector = Guice.createInjector(new FormatterModule)

  Seq("added-as-approver1").foreach { name =>
    test(s"format $name") {
      new FormatterGoldenTest {
        override def formatter: MaybeFormatter =
          injector.getInstance(classOf[AddedAsApproverFormatter])

        override def testCase: String = name
      }
    }
  }
}
