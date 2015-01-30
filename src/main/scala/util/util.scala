package util

import scala.io.Source
import java.net.URLEncoder
import org.json4s._
import org.json4s.jackson.JsonMethods._

import dispatch._
import Defaults._
import scala.util.{Success, Failure}


object Utils {

  private val urlBase = "https://query.yahooapis.com/v1/public/yql?q="
  private val urlSuffix = "&format=json&env=store://datatables.org/alltableswithkeys" 

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

  // def getInfo(table: String, key: String, value: String): Future[String] = {
  //   val yqlQuery = "select * from %s where %s = '%s'" format (table, key, value) 
  //   val yqlUrl = urlBase + yqlQuery + urlSuffix
  //   val request = url(yqlUrl)
  //   val response: Future[String] = Http(request OK as.String)
  //
  //   response
  // }

  def getInfo(table: String, key: String, value: String) = {
    val yqlQuery = "select * from %s where %s = '%s'" format (table, key, value) 
    val yqlUrl = urlBase + URLEncoder.encode(yqlQuery, "utf-8") + urlSuffix
    val jsonStr = Source.fromURL(yqlUrl).mkString
    val json = parse(jsonStr)
    json
  }

}
