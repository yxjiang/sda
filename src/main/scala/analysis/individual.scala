package analysis 

import org.json4s._
import org.json4s.jackson.JsonMethods._
import util.Quotes._
import util.KeyStats._

object IndividualAnalysis {

  def getAdjustPE(symbol: String, jsons: Map[String, JValue]) = {
    val quotesJson = jsons("quotes")
    val keyStatsJson = jsons("keyStats")
    val lastTradePrice = getLastTradePrice(symbol, quotesJson)
    val earningsPerShare = getEarningsPerShare(symbol, quotesJson)
    val cashPerShare = getCashPerShare(symbol, keyStatsJson)
    (lastTradePrice - cashPerShare) / earningsPerShare
  }

  def getCashStockPriceRatio(symbol: String, jsons: Map[String, JValue]) = {
    val quotesJson = jsons("quotes")
    val keyStatsJson = jsons("keyStats")
    val lastTradePrice = getLastTradePrice(symbol, quotesJson)
    val cashPerShare = getCashPerShare(symbol, keyStatsJson)
    cashPerShare / lastTradePrice
  }

  def getEarningsGrowthPERatio(symbol: String, jsons: Map[String, JValue]) = {
    val keyStatsJson = jsons("keyStats")
    val quotesJson = jsons("quotes")
    val earningsGrowth = getQuarterlyEarningsGrowth(symbol, keyStatsJson)
    val PERatio = getPERatio(symbol, quotesJson)

    earningsGrowth / PERatio
  } 

  def getPEAdjustPERatio(symbol: String, jsons: Map[String, JValue]) = {
    val PERatio = getPERatio(symbol, jsons("quotes"))
    val adjustPE = getAdjustPE(symbol, jsons)
    try {
      adjustPE / PERatio 
    } catch {
      case _: Throwable => 1.0
    }
  }
  
}
