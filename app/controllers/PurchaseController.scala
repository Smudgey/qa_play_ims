package controllers

import javax.inject.Inject


import play.api.mvc.{Action, Controller}


// you need this import to have combinators
import play.api.libs.json._
import play.api.libs.functional.syntax._

/**
  * Created by rytis on 09/08/16.
  */

case class PurchaseOrderJSON(id: String, amount: String) {}

object PurchaseOrderJSON {
  implicit val rds: Reads[PurchaseOrderJSON] = (
    (__ \ "itemID").read[String] and
      (__ \ "amount").read[String]
    ) (PurchaseOrderJSON.apply _)
}


class PurchaseController @Inject extends Controller {


  def index() = Action {
    implicit request =>
      Ok(views.html.purchase(request.flash))
  }


  def orderStock() = Action {
    implicit request =>



      val json: JsValue = request.body.asJson.get
      println(json)
      val foo = json.validate[List[PurchaseOrderJSON]].fold(
        error => {
          // There were validation errors, handle them here.
          println(error)
        },
        success => {
          println(success)
        }
      )
      /**/
      /* Redirect(routes.PurchaseController.index()).flashing("type" -> "success", "message" -> Messages("stock.ordered"))*/
      Ok("fail")
  }
}
