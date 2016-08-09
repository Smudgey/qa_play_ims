package models

import reactivemongo.bson.{BSONDocument, BSONDocumentReader}

/**
  * Created by Administrator on 03/08/2016.
  */
case class Product(itemID: String, product: String, images: Array[String], category: Array[String], description: String, var stock: Int, price: Double) {

  def hasXAvailable(x: Int): Boolean = {
    this.stock >= x
  }

  // URL: String
  def decrementStock(quantity: Int): Unit = {
    //Add stock validation here?
    stock = stock - quantity

  }

  def incrementStock(quantity: Int): Unit = {
    //Add stock validation here?
    stock += quantity

  }
}


object Product {

  implicit object productReader extends BSONDocumentReader[Product] {
    def read(doc: BSONDocument): Product =
      Product(
        doc.getAs[String]("itemID").get,
        doc.getAs[String]("Product").get,
        doc.getAs[Array[String]]("images").get,
        doc.getAs[Array[String]]("Category").get,
        doc.getAs[String]("Description").get,
        doc.getAs[Int]("Stock").get,
        doc.getAs[Double]("price").get
      )
  }

}
