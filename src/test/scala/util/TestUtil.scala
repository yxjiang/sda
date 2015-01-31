package util

import org.scalatest._

import scala.io.Source 
import org.json4s._
import org.json4s.jackson.JsonMethods._

import dispatch._
import Defaults._
import scala.util.{Success, Failure}

class TestUtil extends FlatSpec with Matchers {

  val symbol = "GOOG"
  val sectorNo = "113"

  // "getSectors" should "get all the sectors" in {
  //   val sectors = Utils.getSectors
  //   assert(sectors.isInstanceOf[JValue])
  // }
  //
  // "getSectorNames" should "get all the names of the sectors" in {
  //   val sectorNames = Utils.getSectorNames
  //   assert(sectorNames.isInstanceOf[JArray])
  // }
  //
  // "getQuotes" should "get quotes for a specified stock" in {
  //   val quote = Utils.getQuotes(symbol)  
  //   assert(quote.isInstanceOf[JValue])
  // }
  //
  // "getCompaniesInIndustry" should "get all the companies belong to a specified industry domain" in {
  //   val companies = Industry.getCompaniesInIndustry(sectorNo)
  //   println(companies)
  // }
  //
  // "getCashFlow" should "get the cash flow data of the specified stock" in {
  //   val cashflow = Utils.getCashFlow(symbol)
  //   assert(cashflow.isInstanceOf[JValue])
  // }
  //
  // "getBalanceSheet" should "get the balance sheet data of the specified stock" in {
  //   val balanceSheet = Utils.getBalanceSheet(symbol)
  //   println(balanceSheet)
  // }
  //
  // "test" should "test" in {
  //   val request = url("http://api.hostip.info/country.php")
  //   val response: Future[String] = Http(request OK as.String)
  //
  //   response onComplete {
  //     case Success(content) => {
  //       println("Success: " + content)
  //     }
  //     case Failure(t) => {
  //       println("Failure: " + t.getMessage)
  //     }
  //   }
  //
  // }

}
