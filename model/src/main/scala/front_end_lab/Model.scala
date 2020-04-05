package front_end_lab

import java.time.LocalDateTime

import io.circe.generic.JsonCodec

object Model {

  trait HasId[PK] {
    val id: PK
  }

  @JsonCodec
  case class Food(id: Int, name: String, `type`: String, calories: Option[Int], created: LocalDateTime)
      extends HasId[Int]

}
