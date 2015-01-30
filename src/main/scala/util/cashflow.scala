package util

import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.JsonDSL.WithDouble._
import Utils._

object CashFlow {
  
  implicit val formats = DefaultFormats // Brings in default date formats etc.

  /**
   * Get the cashflow data of a specified stock.
   *
   * @param symbol the symbol of the specified stock.
   */
  def getCashFlow(symbol: String) = {
    val json = getInfo("yahoo.finance.cashflow", "symbol", symbol)
    val data = (json \ "query" \ "results" \ "cashflow")
    (data \ "statement") match {
      case JNothing => throw new Exception("Data not available for cash flow for symbol [%s]." format  symbol) 
      case _ => data
    }
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

}
