package util

import org.scalatest._

import org.json4s._
import org.json4s.jackson.JsonMethods._

class TestBalanceSheet extends FlatSpec with Matchers {

  val symbol = "FB"

  "getBalanceSheet" should "get the balance sheet data of the specified stock" in {
    // val balanceSheet = BalanceSheet.getBalanceSheet(symbol)
    // println(balanceSheet)
    // implicit val formats = DefaultFormats
    // val test = Map("test" -> "aa")
    // val res = parse(Serialization.write(test))
    // println(res)
  }

 
  "getReportDates" should "get the report dates for the company" in {
    // val balanceSheet = BalanceSheet.getBalanceSheet(symbol)
    // println(pretty(render(balanceSheet))) 
  }

}
