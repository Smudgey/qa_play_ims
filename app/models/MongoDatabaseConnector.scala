package models

import reactivemongo.api.MongoDriver
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.BSONDocument
import reactivemongo.core.nodeset.Authenticate

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}


/**
  * Created by rytis on 28/07/16.
  */
trait MongoDatabaseConnector {

  object DatabaseNames extends Enumeration {
    type DatabaseNames = Value
    val ACCOUNT_DATABASE = "nb_gardens_accounts"
    val ORDERS_DATABASE = "nb_gardens_orders"
  }

  object CollectionNames extends Enumeration {
    type CollectionNames = Value
    val ACCOUNT_COLLECTION = "accounts"
    val ORDER_COLLECTION = "orders"
    val PRODUCT_COLLECTION = "products"

  }

  private val driver = new MongoDriver

  def connectToDatabase(collectionName: String, databaseName: String): Future[BSONCollection] = {
    val credentials = List(Authenticate(databaseName, "appaccess", "appaccess"))

    def servs: List[String] = List("192.168.1.15:27017")

    val conn = driver.connection(servs, authentications = credentials)

    conn.database(databaseName).map(_.collection(collectionName))
  }

  def getOrderHistory(email: String): ArrayBuffer[Order] = {
    var toReturn = ArrayBuffer[Order]()

    //val accId = findAccountByEmail(email).head.accountID

    //println("acc: "+ accId)

    connectToDatabase(CollectionNames.ORDER_COLLECTION, DatabaseNames.ORDERS_DATABASE).onComplete {
      case Success(result) =>
        println("connected")
        val query = BSONDocument(
          "accountID" -> "108921209"
        )
        val ordersList = result.find(query).cursor[Order].collect[List]()
        ordersList.onComplete {
          case Success(orders) =>
            for (ord <- orders) {
              toReturn += ord
            }
          case Failure(t) => println("failed")
        }
      case Failure(fail) =>
        println("failed")
    }

    Thread.sleep(3000)
    println(toReturn.length)
    toReturn

  }
  
  def findProductByID(itemID: String): Product = {
    var returnHere = ArrayBuffer[Product]()

    connectToDatabase(CollectionNames.PRODUCT_COLLECTION, DatabaseNames.ORDERS_DATABASE).onComplete {
      case Success(result) =>

        val query = BSONDocument(
          "itemID" -> itemID
        )
        result.find(query).one[Product].onComplete {
          case Success(product) =>

            returnHere += Product( product.get.itemID,product.get.product, product.get.images, product.get.category, product.get.description, product.get.stock, product.get.price)

          case Failure(fail) =>
            returnHere
        }
      case Failure(fail) =>
        returnHere
    }
    Thread.sleep(500)
    returnHere.head
  }

  def findByCategory(category: String): Array[Product] = {
    var categoryBuffer = ArrayBuffer[Product]()

    connectToDatabase(CollectionNames.PRODUCT_COLLECTION, DatabaseNames.ORDERS_DATABASE).onComplete {
      case Success(result) =>
        val query = BSONDocument(
          "category" -> category
        )
        result.find(query).one[Product].onComplete {
          case Success(product) =>
            categoryBuffer += Product(product.get.product, product.get.itemID, product.get.images, product.get.category, product.get.description, product.get.stock, product.get.price)

          case Failure(fail) =>
            categoryBuffer
        }
      case Failure(fail) =>
        categoryBuffer
    }
    Thread.sleep(500)
    categoryBuffer.toArray
  }

  def allProducts(itemName: String): Array[Product] = {
    var returnBuffer = ArrayBuffer[Product]()

    connectToDatabase(CollectionNames.PRODUCT_COLLECTION, DatabaseNames.ORDERS_DATABASE).onComplete {

      case Success(result) =>


        val query = BSONDocument()

        result.find(query).cursor[Product].collect[ArrayBuffer]().onComplete {
          case Success(coll) =>
            returnBuffer = coll
        }
    }

    Thread.sleep(500)
    println(returnBuffer.length)
    returnBuffer.toArray
  }
}
