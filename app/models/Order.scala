package models

import reactivemongo.bson.{BSONDocument, BSONDocumentReader}


/**
  * Created by Marko on 02/08/2016.
  */
case class Order(orderID: Int, accountId: String, date: String, time: String, orderStatus: String, paymentMethod: String, orderLines: Array[OrderLine], totalPrice: Double, rating: Int) {}

object Order {
  implicit object OrderReader extends BSONDocumentReader[Order] {
    def read(doc: BSONDocument): Order =
      Order(
        doc.getAs[Int]("orderID").get,
        doc.getAs[String]("accountID").get,
        doc.getAs[String]("date").get,
        doc.getAs[String]("time").get,
        doc.getAs[String]("orderStatus").get,
        doc.getAs[String]("paymentMethod").get,
        doc.getAs[Array[OrderLine]]("orderLines").get,
        doc.getAs[Double]("totalPrice").get,
        doc.getAs[Int]("rating").get
      )
  }
}