package com.astridej.macros.ex3

import cats.effect.Concurrent
import cats.syntax.all.*
import org.http4s.circe.CirceEntityDecoder.*
import org.http4s.circe.CirceInstances.*
import org.http4s.client.Client
import org.http4s.implicits.uri

import java.util.TimeZone

trait WeatherFrog[F[_]] {
  def getTodaysWeather(
      latitude: String,
      longitude: String,
      tz: TimeZone
  ): F[WeatherInfo]
}

case class WeatherInfo(high: Int, low: Int, rainChance: Double) {
  def complaint: List[String] = List(
    Option.when(high < 18)(
      "Weather is too cold."
    ), // 64 Fahrenheit for the Fahrenheit-users among us
    Option.when(high > 30)(
      "Weather is too hot."
    ), // 86 Fahrenheit for the Fahrenheit-users among us
    Option.when(rainChance > 0.5)("It's going to rain :(")
  ).flatten
}

object WeatherFrog {
  // open-meteo.com offers a free weather api, let's use that
  def buildOpenMeteo[F[_]: Concurrent](client: Client[F]): WeatherFrog[F] =
    (latitude: String, longitude: String, tz: TimeZone) => {
      val url = uri"https://api.open-meteo.com/v1/forecast"
        .withQueryParams(
          Map(
            "latitude"  -> latitude,
            "longitude" -> longitude,
            "daily" -> "weathercode,temperature_2m_max,temperature_2m_min,precipitation_probability_max",
            "timezone" -> tz.getID
          )
        )
      client
        .expect[OpenMeteoResponse](url)
        .map(_.toWeatherInfo)
    }

}

case class OpenMetoDailyResponse(
    temperature_2m_max: List[Double],
    weathercode: List[Int],
    temperature_2m_min: List[Double],
    precipitation_probability_max: List[Int]
) derives io.circe.Codec.AsObject

case class OpenMeteoResponse(daily: OpenMetoDailyResponse)
    derives io.circe.Codec.AsObject {
  def toWeatherInfo: WeatherInfo = WeatherInfo(
    daily.temperature_2m_max.head.toInt,
    daily.temperature_2m_min.head.toInt,
    daily.precipitation_probability_max.head.toDouble / 100
  )
}
