package util

import org.scalatest._

import org.json4s._
import org.json4s.jackson.JsonMethods._

class TestReport extends FlatSpec with Matchers {
 
  "getCompanyReport" should "get the report for the company" in {
    val report = Report.generateCompanyReports(List("AAPL", "FB"))

  }
}