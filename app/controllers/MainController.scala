package controllers

import javax.inject.Inject

import play.api.mvc.{Action, Controller}

/**
  * Created by rytis on 08/08/16.
  */
class MainController @Inject extends Controller{
  def index = Action {
    Ok(views.html.index())
  }
}
