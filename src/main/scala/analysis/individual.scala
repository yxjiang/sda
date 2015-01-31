package analysis 

import org.json4s._
import org.json4s.jackson.JsonMethods._
import util.Quotes._
import util.KeyStats._

object IndividualStockAnalysis {

  def getAdjustPE(symbol: String, quotesJson: JValue, keyStatsJson: JValue) = {
    val lastTradePrice = getLastTradePrice(symbol, quotesJson)
    val earningsPerShare = getEarningsPerShare(symbol, quotesJson)
    val cashPerShare = getCashPerShare(symbol, keyStatsJson)
    (lastTradePrice - cashPerShare) / earningsPerShare
  }

  def getCashStockPriceRatio(symbol: String, keyStatsJson: JValue, quotesJson: JValue) = {
    val lastTradePrice = getLastTradePrice(symbol, quotesJson)
    val cashPerShare = getCashPerShare(symbol, keyStatsJson)
    cashPerShare / lastTradePrice
  }

  def getEarningsGrowthPERatio(symbol: String, keyStatsJson: JValue, quotes: JValue) = {
    val earningsGrowth = getQuarterlyEarningsGrowth(symbol, keyStatsJson)
    val PERatio = getPERatio(symbol, quotes)

    earningsGrowth / PERatio
  } 
  
}
