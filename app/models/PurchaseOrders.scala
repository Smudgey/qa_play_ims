package models

import org.mindrot.jbcrypt.BCrypt
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by rytis on 09/08/16.
  */

case class PurchaseOrders(pid: String, orderLine: Array[OrderLine], dateOrdered: String, dateReceived: String, status: String) {}

object PurchaseOrders {
  private val purchaseOrders = Array[PurchaseOrders](
    PurchaseOrders(
      "p0",
      Array[OrderLine](
        OrderLine("702", 5, 11.1)
      ),
      "11/09/2016",
      "",
      "pending")
  )


  def findOrder(pid:String) = purchaseOrders.find(_.pid == pid)

  implicit object AccountReader extends BSONDocumentReader[PurchaseOrders] {
    //inheriting methods pertaining to BSONDocumentReader
    def read(doc: BSONDocument): PurchaseOrders =
    PurchaseOrders(
      doc.getAs[String]("ID").get,
      doc.getAs[Array[OrderLine]]("orderLines").get,
      doc.getAs[String]("dateOrdered").get,
      doc.getAs[String]("dateReceived").get,
      doc.getAs[String]("status").get)

  }

  /**
    * See comments on AccountReader. Very similar.
    */
  implicit object AccountWriter extends BSONDocumentWriter[PurchaseOrders] {
    def write(order: PurchaseOrders): BSONDocument = {
      BSONDocument(
        "ID" -> order.pid,
        "orderLines" -> order.orderLine,
        "dateOrdered" -> order.dateOrdered,
        "dateReceived" -> order.dateReceived,
        "status" -> order.status
      )
    }
  }

  /**
    * Create method, takes in a personCollection and shoves into the database it
    */
  def create(personCollection: BSONCollection, account: PurchaseOrders)(implicit ec: ExecutionContext, writer: BSONDocumentWriter[PurchaseOrders]): Future[Unit] = {
    val writeResult = personCollection.insert(account)
    writeResult.map(_ => {
    })
  }
}


