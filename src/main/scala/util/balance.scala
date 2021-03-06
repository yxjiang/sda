package util

import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.JsonDSL.WithDouble._
import Utils._
import MongoDbManager._ 
import com.mongodb.casbah.Imports._


object BalanceSheet {
  implicit val formats = DefaultFormats

  val collName = "BalanceSheet"

  /**
   * Get the balance sheet data of a specified stock.
   *
   * @param symbol the symbol of the specified stock.
   */
  def getBalanceSheet(symbol: String) = {
    val coll = getCollection(MongoDbManager.dbName, collName) 
    val res = getDoc(coll, symbol, "yahoo.finance.balancesheet", "symbol", List("query", "results", "balancesheet")).get
    res
  }

  def getReportDates(symbol: String, balanceSheetJson: JValue) = {
    try { 
      val jsonList = retrieve(balanceSheetJson, List("period")) 
      jsonList2List(jsonList, "period")
    } catch {
      case _: Throwable => List()
    }
  }

  def getLatestReportDate(symbol: String, balanceSheetJson: JValue) = {
    val res = getReportDates(symbol, balanceSheetJson)
    res.head
  } 

  def getCashAndCashEquivalents(symbol: String, balanceSheetJson: JValue) = {
    try {
      val jsonList = retrieve(balanceSheetJson, List("CashAndCashEquivalents", "content")) 
      jsonList2List(jsonList, "content") map { v => v.toDouble }
    } catch {
      case _: Throwable => {
        Console.err.println("Cannot extract CashAndCashEquivalents for symbol [%s]." format symbol)
        List()
      }
    }
  }

  def getLongTermDebts(symbol: String, balanceSheetJson: JValue) = {
    try {
      val jsonList = retrieve(balanceSheetJson, List("LongTermDebt", "content"))
      jsonList2List(jsonList, "content") map { 
        v => try {
          v.toDouble 
        } catch { 
          case _: Throwable => 0.0 
        } 
      }
    } catch {
        case e : Throwable => throw new Exception("Cannot extract LongTermDebts for symbol [%s]." format symbol) 
    }
  }

  def getShortCurrentLongDebt(symbol: String, balanceSheetJson: JValue) = {
    try {
      val jsonList = retrieve(balanceSheetJson, List("Short_CurrentLongTermDebt", "content"))
      jsonList2List(jsonList, "content") map {
        value => try {
          value.toDouble
        } catch {
          case _: Throwable => 0.0
        }
      }
    } catch {
      case _: Throwable => {
        println("Cannot extract Short_CurrentLongDebt for symbol [%s]." format symbol)
        List()
      }
    }
  }

  def getStockholderEquity(symbol: String, balanceSheetJson: JValue) = {
    try {
      val jsonList = retrieve(balanceSheetJson, List("TotalStockholderEquity", "content")) 
      jsonList2List(jsonList, "content") map {
        value => try {
          value.toDouble
        } catch {
          case _: Throwable => 0.0
        }
      }
    } catch {
      case _: Throwable => {
        println("Cannot extract StockholderEquity for symbol [%s]." format symbol)  
        List()
      } 
    }
  }

}
