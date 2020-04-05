package front_end_lab

import java.time.LocalDateTime

import front_end_lab.Model.Food
import slick.basic.DatabaseConfig
import slick.jdbc.{ H2Profile, JdbcProfile }
import slick.jdbc.H2Profile.api._

import scala.concurrent.{ ExecutionContext, Future }

class FoodTable(tag: Tag) extends Table[Food](tag, "food") with BaseTable[Food, Int] {

  val id       = column[Int]("id", O.AutoInc, O.PrimaryKey)
  val name     = column[String]("name")
  val foodType = column[String]("type")
  val calories = column[Int]("calories")
  val created  = column[LocalDateTime]("created")

  override def * = (id, name, foodType, calories.?, created) <> ((Food.apply _).tupled, Food.unapply)
}

trait FoodRepository {

  implicit val ec: ExecutionContext

  val dbc: DatabaseConfig[H2Profile]

  val foodRepo: FoodRepository

  class FoodRepository extends BaseRepository[Int, Food, FoodTable] {

    //  import dbc.profile.api._

    override val query: TableQuery[FoodTable] = TableQuery[FoodTable]

    override def db: Database = dbc.db

    def selectAndUpdate(id: Int, f: Food => Food): Future[Int] = db.run(
      query
        .filter(_.id === id)
        .result
        .head
        .flatMap { current =>
          query.insertOrUpdate(f(current))
        }
        .transactionally
    )
  }

}
