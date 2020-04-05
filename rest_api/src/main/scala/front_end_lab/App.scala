package front_end_lab
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import org.h2.tools.Server
import slick.basic.DatabaseConfig
import slick.jdbc.H2Profile._

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}
import scala.util.{Failure, Success}

object App extends App with Api with FoodRepository with LazyLogging{
  val config = ConfigFactory.load()
  override implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global
  implicit val as = ActorSystem()
  implicit val mat = ActorMaterializer()
  override val foodRepo: front_end_lab.App.FoodRepository = new FoodRepository()
  override val dbc = DatabaseConfig.forConfig("slick", config)
  override val api = new Api

  Http().bindAndHandle(api.route, "0.0.0.0", 9001).onComplete {
    case Failure(exception) => logger.error("Failed to start server", exception)
    case Success(binding) =>
      logger.info("Server up")
      sys.addShutdownHook {
        Await.ready(binding.unbind(), 3 seconds)
        Await.ready(as.terminate(), 3 seconds)
      }
  }
}
