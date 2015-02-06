package util

import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.JsonDSL.WithDouble._
import Utils._
import MongoDbManager._

object Industry {
 
  implicit val formats = DefaultFormats
  val collName = "Industry"

  /**
   * Get the companies with specified industry id.
   *
   * @param id the industry id
   * @return The list of pairs of (company name, company symbol)
   */
  def getCompaniesInIndustry(id: String) = {
    val coll = getCollection(MongoDbManager.dbName, collName)
    val industry = getDoc(coll, id, "yahoo.finance.industry", "id", List("query", "results", "industry")).get

    val industryId = (industry \ "id").extract[String]
    val industryName = (industry \ "name").extract[String]
    val industryList = (industry \ "company")

    val companyList = for {
      JObject(list) <- industry \ "company" 
      tupleObj <- list
    } yield tupleObj 

    val grouped = companyList.groupBy((tuple) => tuple._1)
    val extractedMap = grouped map {
      (tuple) => {
        tuple._2 map {pair => pair._2.extract[String]}
      }
    }

    extractedMap.head.zip(extractedMap.last) 
  }



}
