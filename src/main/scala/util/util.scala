package util

import scala.io.Source
import java.net.URLEncoder
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.DefaultFormats
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.write

import dispatch._
import Defaults._
import scala.util.{Success, Failure}

import com.mongodb.casbah.Imports._

import java.util.Calendar
import java.util.Date



object Utils {

  private val urlBase = "https://query.yahooapis.com/v1/public/yql?q="
  private val urlSuffix = "&format=json&env=store://datatables.org/alltableswithkeys" 
  val UPDATE_THRESHOLD = 1000.toLong * 60 * 60 * 24 * 30 // 1 month
  // val UPDATE_THRESHOLD = 1000.toLong * 60// 1 minute 

  implicit val formats = DefaultFormats

  /**
   * Shortcuts for unimplemented methods.
   */
  def ??? : Nothing = throw new UnsupportedOperationException("not implemented")

  /**
   * Retrieve the data from the json object.
   */
  def retrieve(json: JValue, keys: List[String]) = {
    keys.foldLeft(json)((left, right) => left \\ right)
  }

  /**
   * Format the double in the US format.
   */
  def doubleFormat(value: Double) = {
    "%,.0f" format value 
  }

  /**
   *  Print the type of the object. 
   */
  def typeof[T: Manifest](obj: T): Manifest[T] = manifest[T]

  /**
   *  Print out log message.
   */
  def log(message: String) = {
    println(Console.BLUE + ("[%s] %s." format (Calendar.getInstance.getTime, message)) + Console.RESET) 
  }

  /**
   *  Retrieve the value list from the json list.
   */
  def jsonList2List(jsonList: JValue, key: String) = {
    for {
      JObject(list) <- jsonList
      JField(key, JString(value)) <- list
    } yield value
  }

  def retry[T](nTimes: Int)(block: => Future[T]): Future[T] = {
    if (nTimes == 0) {
      Future.failed(new Exception("Failed."))
    } else {
      block fallbackTo {
        retry(nTimes - 1) { block }
      }
    }
  }

  def getSectors = {
    val path = getClass.getResource("/sector.json").getPath
    val json = parse(Source.fromFile(path).mkString)
    (json \ "query" \ "results" \ "sector")
  }

  def getSectorNames = {
    // getSectors map { entity => entity }
    getSectors \ "name"
  }

  def getInfo(table: String, key: String, value: String) = {
    val yqlQuery = "select * from %s where %s = '%s'" format (table, key, value) 
    val yqlUrl = urlBase + URLEncoder.encode(yqlQuery, "utf-8") + urlSuffix
    val jsonStr = Source.fromURL(yqlUrl).mkString
    val json = parse(jsonStr)
    json
  }

  /**
   *  Fetch the data from specified place.
   */
  def fetchJson(symbol: String, table: String, key: String, path: List[String]): JValue = {
    val json = getInfo(table, key, symbol)
    val data = retrieve(json, path) 
    data
  }

  /**
   *  Get the corresponding document from the specified collection.
   *  This methdo will first attempts to retrieve the document from db.
   *  If failed, it will grab the data using REST API and update the db.
   */
  def getDoc(coll: MongoCollection, value: String, table: String, key: String, path: List[String], attempts: Int = 3): Option[JValue] = {
    val currentTime = Calendar.getInstance.getTime
    val searchField = MongoDBObject(key -> value) 
    val returnField = MongoDBObject("updateTime" -> 1)
    val retrieveField = MongoDBObject("details" -> 1)

    val docOption = coll.findOne(searchField, returnField)
    val result: Option[JValue] = docOption match {
      case Some(timeDoc) => {
        // check whether the document is out-dated
        val updateTime = timeDoc.getAs[Date]("updateTime").get
        if (currentTime.getTime - updateTime.getTime > UPDATE_THRESHOLD) {
          log("Update out-dated documents for [%s]" format value)
          val json = fetchJson(value, table, key, path)
          val res = updateDoc(value, currentTime, json, coll)
          Some(json)
        } else {  // document is not out-dated
          log("Retrieve from db for [%s]" format value)
          coll.findOne(searchField, retrieveField) match {
            case Some(doc) => {
              val jvalue = parse(doc.toString)
              Some(jvalue \ "details")
            }
            case None => {
              None
            }
          }  
        }
      }
      case None => {  // no record exists
        if (attempts > 0) {
          log("Failed to retrieve document for [%s], try again" format value)
          getDoc(coll, value, table, key, path, attempts - 1)  
        } else {
          log("Failed to retrieve document for [%s] for 3 times, fetch from the REST API" format value)
          val json = fetchJson(value, table, key, path)
          insertDoc(value, currentTime, json, coll) 
          Some(json)
        }
      } 
    }

    val res = result.get
    result
  }

  /**
   *  Insert the document into specified data collection.
   */
  def insertDoc(symbol: String, currentTime: Date, json: JValue, coll: MongoCollection) = {
    val doc = json.values.asInstanceOf[Map[String, Any]]
    coll.insert(MongoDBObject("symbol" -> symbol, "updateTime" -> currentTime, "details" -> doc))
  }

  /**
   *  Update the existing document using symbol as the key.
   */
  def updateDoc(symbol: String, updateTime: Date, json: JValue, coll: MongoCollection) = {
    val query = MongoDBObject("symbol" -> symbol)
    val update = MongoDBObject("updateTime" -> updateTime, "details" -> json)
    val ret = json.values.asInstanceOf[Map[String, Any]]
    coll.update(query, update)
  } 

}
