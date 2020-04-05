package controllers

import javax.inject._
import org.h2.tools.Server
import play.api.http.HeaderNames
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MainController @Inject()(cc: MessagesControllerComponents) extends AbstractController(cc) {

  Future(Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9090").start().run())

  def index = Action{ implicit req =>
    Ok(views.html.frames())
  }
}
