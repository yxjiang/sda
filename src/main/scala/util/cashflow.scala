package util

import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.JsonDSL.WithDouble._
import Utils._
import MongoDbManager._

object CashFlow {
  
  implicit val formats = DefaultFormats // Brings in default date formats etc.
  val collName = "CashFlow"

  /**
   * Get the cashflow data of a specified stock.
   *
   * @param symbol the symbol of the specified stock.
   */
  def getCashFlow(symbol: String) = {
    val coll = getCollection(MongoDbManager.dbName, collName)
    val res = getDoc(coll, symbol, "yahoo.finance.cashflow", "symbol", List("query", "results", "cashflow")).get
    res
  }

  def getDividendsPaid(symbol: String, cashFlowJson: JValue) = {
    try {
      val jsonList = retrieve(cashFlowJson, List("DividendsPaid", "content"))
      jsonList2List(jsonList, "content") map {
        value => try {
          value.toDouble 
        } catch {
          case _: Throwable => 0.0
        } 
      }
    } catch {
      case _: Throwable => {
        Console.err.println("Cannot extract DividendsPaid for symbol [%s]." format symbol)
        List() 
      }
    }
  }

  def getLatestDividendsPaid(symbol: String, cashFlowJson: JValue) = {
    getDividendsPaid(symbol, cashFlowJson).head
  }  

  def getNetIncome(symbol: String, cashFlowJson: JValue) = {
    try {
      val jsonList = retrieve(cashFlowJson, List("NetIncome", "content"))
      jsonList2List(jsonList, "content") map { v => v.toDouble} sum
    } catch {
      case _: Throwable => 0.0
    }
  }

}
