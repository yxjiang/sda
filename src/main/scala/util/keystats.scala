package util

import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.JsonDSL.WithDouble._
import Utils._
import MongoDbManager._

object KeyStats {

  implicit val formats = DefaultFormats // Brings in default date formats etc.
  val collName = "KeyStats"

   /**
   * Get the key statistics of a specific stock.
   */
  def getKeyStats(symbol: String) = {
    val coll = getCollection(MongoDbManager.dbName, collName)
    val res = getDoc(coll, symbol, "yahoo.finance.keystats", "symbol", List("query", "results", "stats")).get
    res
  }

  def getCashPerShare(symbol: String, keyStatsJson: JValue) = {
     try {
       retrieve(keyStatsJson, List("TotalCashPerShare", "content")).extract[String].toDouble
     } catch {
       case _: Throwable => 0.0 
     }
  }

  def getQuarterlyRevenueGrowth(symbol: String, keyStatsJson: JValue) = {
    try {
      val str = retrieve(keyStatsJson, List("QtrlyRevenueGrowth", "content")).extract[String]
      str.substring(0, str.length - 1).toDouble
    } catch {
      case _: Throwable => 0.0
    }
  }

  def getQuarterlyEarningsGrowth(symbol: String, keyStatsJson: JValue) = {
    try {
      val str = retrieve(keyStatsJson, List("QtrlyEarningsGrowth", "content")).extract[String]
      str.substring(0, str.length - 1).toDouble
    } catch {
      case _: Throwable => 0.0
    }
  }

  def getShareOutstanding(symbol: String, keyStatsJson: JValue) = {
    try {
      retrieve(keyStatsJson, List("SharesOutstanding")).extract[String].toDouble
    } catch {
      case _: Throwable => 0.0 
    }
  }

  def getProfitMargin(symbol: String, keyStatsJson: JValue) = {
    try {
      retrieve(keyStatsJson, List("ProfitMargin", "content")).extract[String].toDouble
    } catch {
      case _: Throwable => 0.0
    }
  }

}
