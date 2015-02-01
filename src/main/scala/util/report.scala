package util

import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.JsonDSL.WithDouble._
import analysis.IndividualAnalysis._
import Utils._

object Report {
  
  implicit val formats = DefaultFormats // Brings in default date formats etc.
  
  def generateCompanyReport(symbol: String) = {

    // retrieve data from quotes
    val quotes = Quotes.getQuotes(symbol)
    val lastTradePrice = Quotes.getLastTradePrice(symbol, quotes)
    val PERatio = Quotes.getPERatio(symbol, quotes) 
    val earningsPerShare = Quotes.getEarningsPerShare(symbol, quotes) 

    // retrieve data from balance sheet
    val balanceSheet = BalanceSheet.getBalanceSheet(symbol)
    val latestReportDate = BalanceSheet.getLatestReportDate(symbol, balanceSheet) 
    val cashList = BalanceSheet.getCashAndCashEquivalents(symbol, balanceSheet) 

    val avgCash = doubleFormat(cashList.foldLeft(0.0)(_ + _) / cashList.length)
    val cashIncrease = doubleFormat(cashList.head - cashList.last)

    val longTermDebtList = BalanceSheet.getLongTermDebts(symbol, balanceSheet) 
    val avgLongTermDebt = doubleFormat(longTermDebtList.foldLeft(0.0)(_ + _) / longTermDebtList.length)
    val longTermDebtIncrease = doubleFormat(longTermDebtList.head - longTermDebtList.last)

    val shortCurrentLongDebtList = BalanceSheet.getShortCurrentLongDebt(symbol, balanceSheet) 
    val avgShortCurrentLongDebt = doubleFormat(shortCurrentLongDebtList.foldLeft(0.0)(_ + _) / shortCurrentLongDebtList.length)
    val shortCurrentLongDebtIncrease = doubleFormat(shortCurrentLongDebtList.head - shortCurrentLongDebtList.last)

    val totalStockholderEquityList = BalanceSheet.getStockholderEquity(symbol, balanceSheet) 

    // retrieve data from key stats
    val keyStats = KeyStats.getKeyStats(symbol)
    val cashPerShare = KeyStats.getCashPerShare(symbol, keyStats) 

    val quarterlyRevenueGrowth = "%.2f%%" format KeyStats.getQuarterlyRevenueGrowth(symbol, keyStats) 

    val quarterlyEarningsGrowth = "%.2f%%" format KeyStats.getQuarterlyEarningsGrowth(symbol, keyStats) 
    val shareOutstanding = KeyStats.getShareOutstanding(symbol, keyStats)
    val profitMargin = KeyStats.getProfitMargin(symbol, keyStats) 

    // retrieve data from cash flow
    val cashFlow = CashFlow.getCashFlow(symbol)
    val latestDividendsPaid = CashFlow.getLatestDividendsPaid(symbol, cashFlow) 
    val latestNetIncome = CashFlow.getNetIncome(symbol, cashFlow) 

    // calculate analysis stats
    val adjustPE = getAdjustPE(symbol, Map("quotes" -> quotes, "keyStats" -> keyStats))
    val totalStockholderEquityRatio = totalStockholderEquityList.head / (totalStockholderEquityList.head + longTermDebtList.head)
    val cashStockPriceRatio = getCashStockPriceRatio(symbol, Map("keyStats" -> keyStats, "quotes" -> quotes))
    val earningsGrowthPERatio = getEarningsGrowthPERatio(symbol, Map("keyStats" -> keyStats, "quotes" -> quotes))

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
          ("* Total Stockholder Equity Ratio" -> totalStockholderEquityRatio) ~
          ("Cash Stock Price Ratio" -> cashStockPriceRatio) ~
          ("Earnings Growth PE Ratio" -> earningsGrowthPERatio)
        )
      )
    println(pretty(render(report)))
  }

  def generateCompanyReports(symbols: List[String]) = {
    symbols map (symbol => generateCompanyReport(symbol) )
  }
}
