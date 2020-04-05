package models

import java.time.LocalDateTime

import front_end_lab.Model.Food
import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FoodRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) {

  private val dbConfig: DatabaseConfig[JdbcProfile] = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private class FoodTable(tag: Tag) extends Table[Food](tag, "food") {

    val id       = column[Int]("id", O.PrimaryKey, O.AutoInc)
    val name     = column[String]("name")
    val foodType = column[String]("type")
    val calories = column[Int]("calories")
    val created  = column[LocalDateTime]("created")

    override def * = (id, name, foodType, calories.?, created) <> ((Food.apply _).tupled, Food.unapply)
  }

  private val query = TableQuery[FoodTable]

  def insert(item: Food): Future[Int] = db.run(query += item)

  def insert(items: Seq[Food]): Future[Option[Int]] = db.run(query ++= items)

  def insertWithIdQuery(item: Food): Future[Int] =
    db.run(query returning query.map(_.id) += item)

  def insertWithEntity(item: Food): Future[Food] =
    db.run((query returning query) += item)

  def findById(id: Int): Future[Option[Food]] = db.run(query.filter(_.id === id).result.headOption)

  def update(item: Food): Future[Int] =
    db.run(query.filter(_.id === item.id).update(item))

  def update(id: Int, f: Food => Food): Future[Food] = {
    db.run(query.filter(_.id === id).result.head.flatMap{ food =>
      val updated = f(food)
      query.insertOrUpdate(updated).map(_ => updated)
    }.transactionally)
  }

  def deleteById(id: Int): Future[Int] = db.run(query.filter(_.id === id).delete)

  def findAll(): Future[Seq[Food]] = db.run(query.result)
}
