package util

import org.scalatest._

import Utils._
import org.json4s._
import org.json4s.jackson.JsonMethods._
import com.mongodb.casbah.Imports._
import BalanceSheet._

import java.util.Calendar


class TestIndomain extends FlatSpec with Matchers {

  "getClient" should "get the client object" in {

    // val dbName = "sda"
    // val collName = "test"
    //
    // val db = MongoDbManager.getDb(dbName)
    // println(db)
    // val coll = MongoDbManager.getCollection(collName, db)
    // println(manOf(coll))
    //
    // // try insert
    // val symbols = List("FB", "TSLA")
    // symbols.foreach { symbol => {
    //     val balanceSheet = getBalanceSheet(symbol)
    //     val map = Map("stock" -> symbol, "detail" -> balanceSheet)
    //     coll.insert(map) 
    //   }
    // }
  }

}
