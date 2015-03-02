package analysis 

import org.json4s._
import org.json4s.jackson.JsonMethods._
import util.Quotes._
import util.BalanceSheet._
import util.CashFlow._
import util.KeyStats._
import util.Industry._
import util.Report._
import IndividualAnalysis._

import scala.math._
import util.Utils._

object InDomainAnalysis {
  
  def getCashStockPriceRatioList(domainId: String, filter: Tuple2[String, String] => Boolean, blackList: Set[String] = Set()) = {
    
    val companyList = getCompaniesInIndustry(domainId).filter(filter).filter(company => !blackList.contains(company._2))
    val metricList = companyList.map {
      company => {
        val symbol = company._2
        val jsons = Map("quotes" -> getQuotes(symbol), "keyStats" -> getKeyStats(symbol))
        val cashStockPriceRatio = getCashStockPriceRatio(symbol, jsons)
        (company._1, company._2, cashStockPriceRatio)
      }
    }
    metricList
  }

  def getEarningsGrowthPERatioList(domainId: String, filter: Tuple2[String, String] => Boolean, blackList: Set[String] = Set()) = {
    val companyList = getCompaniesInIndustry(domainId).filter(filter).filter(company => !blackList.contains(company._2))
    val metricList = companyList.map {
      company => {
        val symbol = company._2
        val jsons = Map("quotes" -> getQuotes(symbol), "keyStats" -> getKeyStats(symbol))
        val earningsGrowthPERatio = getEarningsGrowthPERatio(symbol, jsons)
        (company._1, company._2, earningsGrowthPERatio)
      }
    }
    metricList
  }

  def getPEAdjustPERatioList(domainId: String, filter: Tuple2[String, String] => Boolean, blackList: Set[String] = Set()) = {
    val companyList = getCompaniesInIndustry(domainId).filter(filter).filter(company => !blackList.contains(company._2))
    val metricList = companyList.map {
      company => {
        val symbol = company._2
        val jsons = Map("quotes" -> getQuotes(symbol), "keyStats" -> getKeyStats(symbol))
        val earningsGrowthPERatio = getPEAdjustPERatio(symbol, jsons)
        (company._1, company._2, earningsGrowthPERatio)
      }
    }
    metricList
  }

  def compoundRanking(domainId: String, filter: Tuple2[String, String] => Boolean, 
    metricFuncList: List[(String, Tuple2[String, String] => Boolean, Set[String]) => List[(String, String, Double)]], 
    topPercentage: Double, blackList: Set[String] = Set()) = {

    val metricSetList = for {
      metricFunc <- metricFuncList
    } yield metricFunc(domainId, filter, blackList) 

    val head = metricSetList.head
    val size = round((head.length - blackList.size) * topPercentage).toInt

    // ranking the first list and extract the symbol only
    val topInHead = metricSetList.head.sortBy(_._3).slice(0, size).map(v => v._2) 
    var symbolSet = Set(topInHead: _*)

    // iteratively take the intersection
    metricSetList.tail.foreach {
      v => { 
        val tops = v.sortBy(_._3).slice(0, size).map(v => v._2)
        symbolSet = symbolSet & Set(tops: _*)
      }
    }

    symbolSet
  }

  def filter(domainId: String, filterFunc: (JValue) => Boolean) = {
    // default symbol filter
    def symbolFilter(x: Tuple2[String, String]) = { 
      x._2.contains(".") == false
    }

    val companyList = getCompaniesInIndustry(domainId).filter(symbolFilter).map(pair => pair._2)
    val companyReportList = generateCompanyReports(companyList.toSet)
    val filteredCompanyReportList = companyReportList.filter(filterFunc).map(report => pretty(report))
    print(filteredCompanyReportList)
  }

  

}
