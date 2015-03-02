package util

import org.scalatest._

import org.json4s._
import org.json4s.jackson.JsonMethods._
import analysis.InDomainAnalysis._
import analysis.IndividualAnalysis._


class TestReport extends FlatSpec with Matchers {
 
  "getCompanyReport" should "get the report for the company" in {
    // val candidates = List("EOG")

    val domainId = "340"

    def filter(x: Tuple2[String, String]) = { 
      x._2.contains(".") == false
    }

    // val candidate = List("CXO", "AR", "UNT", "SYRG", "DRQ", "FET", "MNST", "KEQU")
    // val blackList = Set[String]("PSE", "ACMP", "CNNX")
    // val topPercentage = 0.99
    // val metricList = List(getCashStockPriceRatioList _, getEarningsGrowthPERatioList _, getPEAdjustPERatioList _)
    // val list = compoundRanking(domainId, filter, metricList, topPercentage, blackList)
    // println("Final results:" + list)

    // val list = Set("JBLU", "UHAL", "PGR", "EOG", "DRQ", "FET", "MNST", "KEQU", "AXP", "CCK", "SBUX")
    // val list = Set("QIHU", "GPS", "AXP", "DE", "TRIP")

    // val report = Report.generateCompanyReports(list)
    // println(report) 
  }

}
