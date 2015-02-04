package analysis

import org.scalatest._

import org.json4s._
import org.json4s.jackson.JsonMethods._

import analysis.InDomainAnalysis._
import analysis.IndividualAnalysis._

class TestIndomain extends FlatSpec with Matchers {
  "getCompaniesMetric" should "get the metrics of the companies" in {
    val domainId = "112"

    def filter(x: Tuple2[String, String]) = { 
      x._2.contains(".") == false
    }

    val metricList = List(getCashStockPriceRatioList _, getEarningsGrowthPERatioList _, getPEAdjustPERatioList _)
    val res = compoundRanking(domainId, filter, metricList, 0.6)

  }
}
