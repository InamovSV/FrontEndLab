package front_end_lab

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import front_end_lab.Model.Food
import io.circe.generic.JsonCodec
import io.scalaland.chimney.dsl._

object Dto {
  def parseTime(str: String) = {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
    LocalDateTime.parse(str, formatter)
  }

  @JsonCodec
  case class CreateFood(name: String, `type`: String, calories: Option[Int], created: String) {
    def toEntity: Food = {
      val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
      this
        .into[Food]
        .withFieldConst(_.id, 0)
        .withFieldComputed(_.created, x => LocalDateTime.parse(x.created, formatter))
        .transform
    }
  }

  @JsonCodec
  case class CreateOrUpdateFood(id: Option[Int],
                                name: String,
                                `type`: String,
                                calories: Option[Int],
                                created: Option[String])

  @JsonCodec
  case class UpdateFood(id: Int, name: String, `type`: String, calories: Option[Int]) {
    def toEntity(id: Int, created: LocalDateTime): Food =
      this
        .into[Food]
        .withFieldConst(_.id, id)
        .withFieldConst(_.created, created)
        .transform
  }

}
