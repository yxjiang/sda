package analysis 

import org.json4s._
import org.json4s.jackson.JsonMethods._
import util.Quotes._
import util.BalanceSheet._
import util.CashFlow._
import util.KeyStats._
import util.Industry._
import IndividualAnalysis._

object InDomainAnalysis {
  
  def getCashStockPriceRatioRanking(domainId: String, filter: Tuple2[String, String] => Boolean) = {
    
    val companyList = getCompaniesInIndustry(domainId).filter(filter)
    val metricList = companyList.map {
      company => {
        val symbol = company._2
        val jsons = Map("quotes" -> getQuotes(symbol), "keyStats" -> getKeyStats(symbol))
        val cashStockPriceRatio = getCashStockPriceRatio(symbol, jsons)
        (company._1, company._2, cashStockPriceRatio)
      }
    }
    metricList.sortBy(-_._3)
  }

  def getEarningsGrowthPERatioRanking(domainId: String, filter: Tuple2[String, String] => Boolean) = {
    val companyList = getCompaniesInIndustry(domainId).filter(filter)
    val metricList = companyList.map {
      company => {
        val symbol = company._2
        val jsons = Map("quotes" -> getQuotes(symbol), "keyStats" -> getKeyStats(symbol))
        val earningsGrowthPERatio = getEarningsGrowthPERatio(symbol, jsons)
        (company._1, company._2, earningsGrowthPERatio)
      }
    }
    metricList.sortBy(-_._3)
  }

  def getPEAdjustPERatioRanking(domainId: String, filter: Tuple2[String, String] => Boolean) = {
    val companyList = getCompaniesInIndustry(domainId).filter(filter)
    val metricList = companyList.map {
      company => {
        val symbol = company._2
        val jsons = Map("quotes" -> getQuotes(symbol), "keyStats" -> getKeyStats(symbol))
        val earningsGrowthPERatio = getPEAdjustPERatio(symbol, jsons)
        (company._1, company._2, earningsGrowthPERatio)
      }
    }
    metricList.sortBy(_._3)
  }

  def compoundRanking(metricList: List[(String, Tuple2[String, String]) => (String, String, Double)]) = {
  
  }

}
