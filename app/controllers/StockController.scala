package controllers

import javax.inject.Inject

import models.MongoDatabaseConnector
import play.api.Play.current
import play.api.data.Form
import play.api.data.Forms.{single, text}
import play.api.i18n.Messages
import play.api.i18n.Messages.Implicits._
import play.api.mvc.{Action, Controller}

/**
  * Created by rytis on 08/08/16.
  */
class StockController @Inject extends Controller with MongoDatabaseConnector {


  val productForm = Form(
    single(
      "pid" -> text
    )
  )

  def viewStock() = Action {
    implicit request =>
      Ok(views.html.viewStock(findProductByID(productForm.bindFromRequest().data("pid"))))
  }

  def index = Action {
    implicit request =>
      Ok(views.html.stock(request.flash))
  }

  //TODO
  // decrement stock using order
  def decrementStock = ???

  //TODO
  def discontinue = ???

  //TODO
  def query = ???

}
