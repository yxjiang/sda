package util

import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.JsonDSL.WithDouble._
import Utils._

object Report {
  
  implicit val formats = DefaultFormats // Brings in default date formats etc.
  
  def generateCompanyReport(symbol: String) = {

    // retrieve data from quotes
    val quotes = getQuotes(symbol)
    val lastTradePrice = retrieve(quotes, List("LastTradePriceOnly")).extract[String].toDouble
    val PERatio = retrieve(quotes, List("PERatio")).extract[String].toDouble
    val earningPerShare = retrieve(quotes, List("EarningsShare")).extract[String].toDouble

    // retrieve data from balance sheet
    val balanceSheet = getBalanceSheet(symbol)
    val latestReportDate = jsonList2List(retrieve(balanceSheet, List("period")), "period").head
    val cashAndCashEquivalents = retrieve(balanceSheet, List("CashAndCashEquivalents", "content"))

    val cashList = jsonList2List(cashAndCashEquivalents, "content") map (value => value.toDouble)

    val avgCash = doubleFormat(cashList.foldLeft(0.0)(_ + _) / cashList.length)
    val cashIncrease = doubleFormat(cashList.head - cashList.last)

    val longTermDebts = retrieve(balanceSheet, List("LongTermDebt", "content"))
    val longTermDebtList = jsonList2List(longTermDebts, "content") map (value => value.toDouble)
    val avgLongTermDebt = doubleFormat(longTermDebtList.foldLeft(0.0)(_ + _) / longTermDebtList.length)
    val longTermDebtIncrease = doubleFormat(longTermDebtList.head - longTermDebtList.last)

    val shortCurrentLongDebt = retrieve(balanceSheet, List("Short_CurrentLongTermDebt", "content"))
    val shortCurrentLongDebtList = jsonList2List(shortCurrentLongDebt, "content") map (value => try { value.toDouble } catch { case _: Throwable => 0.0 })
    val avgShortCurrentLongDebt = doubleFormat(shortCurrentLongDebtList.foldLeft(0.0)(_ + _) / shortCurrentLongDebtList.length)
    val shortCurrentLongDebtIncrease = doubleFormat(shortCurrentLongDebtList.head - shortCurrentLongDebtList.last)

    val totalStockholderEquity = retrieve(balanceSheet, List("TotalStockholderEquity", "content"))
    val totalStockholderEquityList = jsonList2List(totalStockholderEquity, "content") map (value => try { value.toDouble })

    // retrieve data from key stats
    val keyStats = getKeyStats(symbol)
    val cashPerShare = retrieve(keyStats, List("TotalCashPerShare", "content")).extract[String].toDouble

    val quarterlyRevenueGrowthStr = retrieve(keyStats, List("QtrlyRevenueGrowth", "content")).extract[String]
    val quarterlyRevenueGrowth = "%.2f%%" format quarterlyRevenueGrowthStr.substring(0, quarterlyRevenueGrowthStr.length - 1).toDouble

    val quarterlyEarningsGrowthStr = retrieve(keyStats, List("QtrlyEarningsGrowth", "content")).extract[String]
    val quarterlyEarningsGrowth = "%.2f%%" format quarterlyEarningsGrowthStr.substring(0, quarterlyEarningsGrowthStr.length - 1).toDouble
    val shareOutstanding = retrieve(keyStats, List("SharesOutstanding")).extract[String].toDouble
    val profitMargin = retrieve(keyStats, List("ProfitMargin", "content")).extract[String]

    // retrieve data from cash flow
    val cashFlow = getCashFlow(symbol)
    val latestDividendsPaid = try { 
      val dividendList = retrieve(cashFlow, List("DividendPaid", "content"))
      jsonList2List(dividendList, "content").head.toDouble
    } catch { 
      case _: Throwable => 0.0 
    }

    // calculate analysis stats
    val latestNetIncome = jsonList2List(retrieve(cashFlow, List("NetIncome", "content")), "content") map ( x => x.toDouble ) sum
    val adjustPE = (lastTradePrice - cashPerShare) / earningPerShare 
    val totalStockholderEquityRatio = totalStockholderEquityList.head / (totalStockholderEquityList.head + longTermDebtList.head)

    // construct report json
    val report = 
      (
        ("Latest Report Date" -> latestReportDate) ~
        ("symbol" -> symbol) ~ 
        ("P/E" -> PERatio) ~ 
        ("Total Stockholder Equity" -> (totalStockholderEquityList map {equity => "%,.0f" format equity.toDouble})) ~
        ("Revenue" ->
          ("Quarterly Cash" -> (cashList map {cash => "%,.0f" format cash.toDouble})) ~
          ("Average Cash" -> avgCash) ~ 
          ("* Cash Increase" -> cashIncrease) ~
          ("* Cash Per Share" -> cashPerShare) ~
          ("Profit Margin" -> profitMargin) ~
          ("Querterly Revenue Growth (yoy)" -> quarterlyRevenueGrowth) ~
          ("Querterly Earnings Growth (yoy)" -> quarterlyEarningsGrowth)
        ) ~
        ("Debt" -> 
          ("Quarterly Long Term Debt" -> (longTermDebtList map {debt => "%,.0f" format debt.toDouble})) ~
          ("Average Long Term Debt" -> avgLongTermDebt) ~
          ("* Long Term Debt Increase" -> longTermDebtIncrease) ~
          ("Quarterly Short Current Long Term Debt" -> (shortCurrentLongDebtList map (debt => "%,.0f" format debt.toDouble))) ~
          ("Average Short Current Long Term Debt" -> avgShortCurrentLongDebt) ~
          ("Short Current Long Term Debt Increase" -> shortCurrentLongDebtIncrease)
        ) ~ 
        ("Analytics" ->
          ("* Adjusted P/E" -> adjustPE) ~
          ("* Total Stockholder Equity Ratio" -> totalStockholderEquityRatio)
        )
      )
    println(pretty(render(report)))
  }

  def generateCompanyReports(symbols: List[String]) = {
    symbols map (symbol => generateCompanyReport(symbol) )
  }
}
