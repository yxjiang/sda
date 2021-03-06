package analysis

import org.scalatest._

import org.json4s._
import org.json4s.jackson.JsonMethods._

import util.Utils._
import analysis.InDomainAnalysis._
import analysis.IndividualAnalysis._

class TestIndomain extends FlatSpec with Matchers {

  val domainId = "120"

  "getCompaniesMetric" should "get the metrics of the companies" in {
    // val domainId = "120"
    //
    // def filter(x: Tuple2[String, String]) = { 
    //   x._2.contains(".") == false
    // }
    //
    // val blackList = Set[String]()
    // val metricList = List(getCashStockPriceRatioList _, getEarningsGrowthPERatioList _, getPEAdjustPERatioList _)
    // val res = compoundRanking(domainId, filter, metricList, 0.7, blackList)
    // println("Final results:" + res)
  }

  "filter" should "filter the companies in the domain" in {
    def filterFunc(report: JValue) = {
      val pe = retrieve(report, List("P/E")).extract[String].toDouble
      if (pe < 15 && pe > 0) true else false
    } 

    filter(domainId, filterFunc) 
  }

}
