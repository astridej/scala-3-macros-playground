package testing

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.circe.CirceInstances.*
import org.http4s.circe.CirceEntityDecoder.*

import java.io.File
import java.time.Instant
import scala.quoted.{Expr, Quotes, ToExpr}

case class WeatherInfo(high: Int, low: Int, rainChance: Double) {
  def complaint: Option[String] = List(
    Option.when(high < 18)("Weather is too cold."),
    Option.when(high >= 30)("Weather is too hot."),
    Option.when(rainChance > 0.7)("It's going to rain :(")
  ).flatten.headOption
}

object WeatherInfo {
  given ToExpr[WeatherInfo] = new ToExpr[WeatherInfo]:
    override def apply(x: WeatherInfo)(using Quotes): Expr[WeatherInfo] = '{
      WeatherInfo(${ Expr(x.high) }, ${ Expr(x.low) }, ${ Expr(x.rainChance) })
    }
}

case class OpenMetoDailyResponse(
    temperature_2m_max: List[Double],
    weathercode: List[Int],
    temperature_2m_min: List[Double],
    precipitation_probability_max: List[Int]
) derives io.circe.Codec.AsObject

case class OpenMeteoResponse(daily: OpenMetoDailyResponse) derives io.circe.Codec.AsObject {
  def toWeatherInfo: WeatherInfo = WeatherInfo(
    daily.temperature_2m_max.head.toInt,
    daily.temperature_2m_min.head.toInt,
    daily.precipitation_probability_max.head.toDouble / 100
  )
}

def unwiseWeatherFrogCode()(using quotes: Quotes): Expr[WeatherInfo] = {
  // 🐸
  val weatherInfo =
    EmberClientBuilder
      .default[IO]
      .build
      .use { client =>
        client
          .expect[OpenMeteoResponse](
            "https://api.open-meteo.com/v1/forecast?latitude=52.52&longitude=13.41&daily=weathercode,temperature_2m_max,temperature_2m_min,precipitation_probability_max&timezone=Europe%2FBerlin"
          )
          .map(_.toWeatherInfo)
      }
      .unsafeRunSync()
  weatherInfo.complaint match {
    case Some(complaint) =>
      quotes.reflect.report.errorAndAbort(s"The weather is bad because $complaint, so the compiler is sulking.")
    case None =>
      println("The weather is good, the compiler can continue working!")
      Expr(weatherInfo)
  }
}

case class BuildInfo(time: Instant, gitCommit: String)

object BuildInfo {
  given ToExpr[BuildInfo] = new ToExpr[BuildInfo]:
    override def apply(x: BuildInfo)(using Quotes): Expr[BuildInfo] = '{
      BuildInfo(
        Instant.ofEpochSecond(${ Expr(x.time.getEpochSecond) }, ${ Expr(x.time.getNano) }),
        ${ Expr(x.gitCommit) }
      )
    }
}

def buildInfoCode()(using quotes: Quotes): Expr[BuildInfo] = {
  import sys.process.*
  val now                 = Instant.now()
  val currentFileLocation = quotes.reflect.Position.ofMacroExpansion.sourceFile.jpath
  val process = Process("git rev-parse HEAD", new File(currentFileLocation.getParent().toAbsolutePath().toString))
  Expr(BuildInfo(now, process.!!))
}
