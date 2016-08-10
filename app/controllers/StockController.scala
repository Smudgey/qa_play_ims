package controllers

import javax.inject.Inject

import models.{MongoDatabaseConnector, OrderLine}
import play.api.Play.current
import play.api.data.Form
import play.api.data.Forms.{single, text}
import play.api.i18n.Messages
import play.api.i18n.Messages.Implicits._
import play.api.mvc.{Action, Controller}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject._
import akka.actor.{ActorRef, ActorSystem}
import com.rabbitmq.client.{ConnectionFactory, DefaultConsumer}
import com.thenewmotion.akka.rabbitmq.{ChannelActor, ChannelMessage, CreateChannel, _}

/**
  * Created by rytis on 08/08/16.
  */
class StockController @Inject extends Controller with MongoDatabaseConnector with App{


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

  //Calling this method will trigger a subscriber to receive all the orderlines from the main play application
  def startSubscriber: Unit = {
    implicit val system = ActorSystem()

    val HOST_ADDRESS: String = "192.168.1.15"
    val USER_NAME: String = "jesus"
    val USER_PASSWORD: String = "jesus"

    val factory = new ConnectionFactory()
    factory.setHost(HOST_ADDRESS)
    factory.setUsername(USER_NAME)
    factory.setPassword(USER_PASSWORD)
    factory.setConnectionTimeout(60000)
    val connection = system.actorOf(ConnectionActor.props(factory), "rabbitmq")
    val exchange = "amq.fanout"

    def fromBytes(x: Array[Byte]) = new String(x, "UTF-8")

    def setupSubscriber(channel: Channel, self: ActorRef) {
      val queue = channel.queueDeclare().getQueue
      channel.queueBind(queue, exchange, "")
      val consumer = new DefaultConsumer(channel) {
        override def handleDelivery(consumerTag: String, envelope: Envelope, properties: BasicProperties, body: Array[Byte]) {
          //Convert bytes to orderline object
          //will be of time "id,quant,price"
          val cols = fromBytes(body).split(',').map(_.trim)
          val orderline = new OrderLine(cols(0), cols(1).toInt, cols(2).toInt)

          println("received: " + fromBytes(body))
          //pass orderline to decrementStock method
          //decrementStock(orderline)
        }
      }
      channel.basicConsume(queue, true, consumer)
    }

    def closeConnection() ={
      system stop connection
    }

    connection ! CreateChannel(ChannelActor.props(setupSubscriber), Some("subscriber"))
  }

}
