package controllers

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import front_end_lab.Dto.{CreateFood, CreateOrUpdateFood, UpdateFood, parseTime}
import front_end_lab.Model.Food
import javax.inject._
import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.i18n._
import play.api.libs.json.Json
import play.api.mvc._
import io.scalaland.chimney.dsl._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class FoodController @Inject()(repo: FoodRepository, cc: MessagesControllerComponents)(implicit exec: ExecutionContext) extends MessagesAbstractController(cc) {

  //  repo.insert(Seq.fill(5)(Food(0, "somefood", "delicious", Some(100), LocalDateTime.now)))

  val createFoodForm = Form {
    mapping(
      "name" -> nonEmptyText,
      "type" -> text,
      "calories" -> optional(number),
      "created" -> text
    )(CreateFood.apply)(CreateFood.unapply)
  }

  val updateOrUpdateFoodForm = Form {
    mapping(
      "id" -> optional(number),
      "name" -> nonEmptyText,
      "type" -> text,
      "calories" -> optional(number),
      "created" -> optional(text)
    )(CreateOrUpdateFood.apply)(CreateOrUpdateFood.unapply)
  }

  val updateFoodForm = Form {
    mapping(
      "id" -> number,
      "name" -> nonEmptyText,
      "type" -> text,
      "calories" -> optional(number),
    )(UpdateFood.apply)(UpdateFood.unapply)
  }

  def getFoods = repo.findAll()

  def index = Action.async { implicit request =>
    getFoods.map(foods => Ok(views.html.foods(createFoodForm, updateFoodForm)(foods)))
  }

  def create = Action.async { implicit request =>
    createFoodForm.bindFromRequest().fold(badRequestForm, createFood => {
      repo.insert(createFood.toEntity).map(_ => redirectToSelf)
    })
  }

  def update = Action.async { implicit request =>
    updateFoodForm.bindFromRequest().fold(badRequestForm, updateFood => { import updateFood._
      repo.update(id, _.copy(name = name, `type` = `type`, calories = calories)).map { _ =>
        redirectToSelf
      }
    })
  }

  def createOrUpdate = Action.async { implicit request =>

    val form = updateOrUpdateFoodForm.bindFromRequest()
    form.fold(
      badRequestForm,
      e => e match {
        case updateReq if updateReq.id.isDefined => import updateReq._
          repo.update(updateReq.id.get, _.copy(name = name, `type` = `type`, calories = calories)).map { _ =>
            redirectToSelf
          }
        case createReq =>
          val entity = createReq.into[Food]
            .withFieldConst(_.id, 0)
            .withFieldConst(_.created, createReq.created.map(parseTime).getOrElse(LocalDateTime.now))
            .transform
          repo.insert(entity).map { _ =>
            redirectToSelf
          }
      }
    )
  }

  def delete(id: Int) = Action.async { implicit request =>
    repo.deleteById(id.toInt).map(_ => redirectToSelf)
  }

  def badRequestForm(form: Form[_]) = {
    Future.successful(BadRequest(s"Bad request: ${form.errors.map(e => s"${e.key} -> ${e.messages.mkString("[", ",", "]")}").mkString("\n", "\n", "")}"))
  }

  def redirectToSelf = Redirect(routes.FoodController.index())

}
