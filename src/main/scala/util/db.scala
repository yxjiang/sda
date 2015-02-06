package util

import com.mongodb.casbah.Imports._

object MongoDbManager {
  val dbUrl = "localhost"
  val port = 27017
  val dbName = "sda"

  private val mongoClient = MongoClient(dbUrl, port)

  /**
   *  Create or get the specified database.
   */
  def getDb(dbName: String): MongoDB = mongoClient(dbName)

  /**
   *  Get the specified collection.
   */
  def getCollection(collName: String, db: MongoDB): MongoCollection = db(collName) 

  /**
   *  Get the specific collection.
   */
  def getCollection(dbName: String, collName: String): MongoCollection = getCollection(collName, getDb(dbName))

}
