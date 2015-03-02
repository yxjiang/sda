package util

import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.JsonDSL.WithDouble._
import Utils._
import MongoDbManager._

object Quotes {

  implicit val formats = DefaultFormats
  val collName = "Quotes"

  /**
   * Get the quotes of the stock by symbol.
   *
   * @param symbol the symbol of the specified stock
   */
  def getQuotes(symbol: String): JValue = {
    val coll = getCollection(MongoDbManager.dbName, collName)
    val res = getDoc(coll, symbol, "yahoo.finance.quotes", "symbol", List("query", "results", "quote"))
    res
  } 

  def getName(symbol: String, quotesJson: JValue) = {
    try {
      retrieve(quotesJson, List("Name")).extract[String]
    } catch {
      case _: Throwable => throw new Exception("Cannot extract Name for symbol [%s]." format symbol)
    }

  }
  
  def getLastTradePrice(symbol: String, quotesJson: JValue) = {
    try {
      retrieve(quotesJson, List("LastTradePriceOnly")).extract[String].toDouble
    } catch {
      case _: Throwable => throw new Exception("Cannot extract LastTradePriceOnly for symbol [%s]." format symbol)
    }
  }

  def getPERatio(symbol: String, quotesJson: JValue) = {
    try {
      val str = retrieve(quotesJson, List("PERatio")).extract[String]
      str.toDouble
    } catch {
      case n: NullPointerException => 0.0
      case _: Throwable => throw new Exception("Cannot extract PERatio for symbol [%s]." format symbol)
    } 
  }

  def getEarningsPerShare(symbol: String, quotesJson: JValue) = {
    try {
      val str = retrieve(quotesJson, List("EarningsShare")).extract[String]
      str.toDouble
    } catch {
      case _: Throwable => throw new Exception("Cannot extract EarningsShare for symbol [%s]." format symbol)
    } 
  }

}
