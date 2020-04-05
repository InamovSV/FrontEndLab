package front_end_lab

import akka.event.Logging
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.directives.DebuggingDirectives
import akka.http.scaladsl.server.{Directives, Route}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import front_end_lab.Dto._

import scala.util.{Failure, Success}

trait Api extends FoodRepository {

  val api: Api

  class Api extends Directives with FailFastCirceSupport with CORSHandler {

    def route: Route = DebuggingDirectives.logRequest("Client ReST", Logging.InfoLevel)(
      corsHandler(pathPrefix("api")(v1))
    )

    private def v1 = pathPrefix("v1") {
      create ~ update ~ remove ~ find ~ findAll
    }

    import io.circe.syntax._

    private def create = post {
      entity(as[CreateFood]) { e =>
        complete(foodRepo.insert(e.toEntity))
      }
    }

    private def update = pathPrefix(IntNumber) { id =>
      put {
        entity(as[UpdateFood]) { e =>
          complete(foodRepo.selectAndUpdate(id, food => e.toEntity(id, food.created)))
        }
      }
    }


    private def remove = pathPrefix(IntNumber) { id =>
      delete {
        complete(foodRepo.deleteById(id))
      }
    }

    private def find = pathPrefix(IntNumber) { id =>
      get {
        onComplete(foodRepo.findById(id)) {
          case Success(Some(value)) => complete(HttpResponse(entity = HttpEntity(ContentTypes.`application/json`, value.asJson.noSpaces)))
          case Success(None) => complete(StatusCodes.NotFound)
          case Failure(exception) => complete(StatusCodes.InternalServerError, HttpEntity(exception.toString))
        }
      }
    }

    private def findAll =
      get {
        complete(foodRepo.findAll())
      }
  }

}
