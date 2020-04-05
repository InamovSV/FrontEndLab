package front_end_lab

import front_end_lab.Model.HasId
import slick.jdbc.H2Profile.api._

import scala.concurrent.Future

trait BaseTable[E, PK] {
  self: Table[E] =>

  val id: Rep[PK]
}

abstract class BaseRepository[PK: BaseColumnType, E <: HasId[PK], T <: Table[E] with BaseTable[E, PK]]
    extends GenericRepo[PK, E] {

  val query: TableQuery[T]

  def db: Database

  def insert(item: E): Future[Int] = db.run(query += item)

  def insert(items: Seq[E]): Future[Option[Int]] = db.run(query ++= items)

  def insertWithIdQuery(item: E): Future[PK] =
    db.run(query returning query.map(_.id) += item)

  override def insertWithEntity(item: E): Future[E] =
    db.run((query returning query) += item)

  def findById(id: PK): Future[Option[E]] = db.run(query.filter(_.id === id).result.headOption)

  def findBy(predicate: T => Rep[Boolean]): Future[Seq[E]] =
    db.run(query.filter(predicate).result)

  def update(item: E): Future[Int] =
    db.run(query.filter(_.id === item.id).update(item))

  def deleteById(id: PK): Future[Int] = db.run(query.filter(_.id === id).delete)

  override def findAll(): Future[Seq[E]] = db.run(query.result)
}
