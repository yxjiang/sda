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

    // val res = getCashStockPriceRatioRanking(domainId, filter)
    // val res = getEarningsGrowthPERatioRanking(domainId, filter)
    val res = getPEAdjustPERatioRanking(domainId, filter)

    res.foreach {
      v => println(v)
    }

  }
}
