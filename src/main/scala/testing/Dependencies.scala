package testing

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

trait Eq[-C] {
  def equals(c: C, d: C): Boolean
}

object Eq {
  given numeric[N](using num: Numeric[N]): Eq[N] = (c, d) => num.equiv(c, d)
  given Eq[String]                               = (c, d) => c == d
  given Eq[Boolean]                              = (c, d) => c == d
  given coll[C](using eq: Eq[C]): Eq[Iterable[C]] = (c: Iterable[C], d: Iterable[C]) =>
    (c.headOption, d.headOption) match {
      case (None, None)                                          => true
      case (Some(chead), Some(dhead)) if eq.equals(chead, dhead) => this.equals(c.tail, d.tail)
      case _                                                     => false
    }
}

trait Encoder[C] {
  def encode(c: C): String
}

trait HasCanonicalRepr[C] {
  def canonical(): C
}

object HasCanonicalRepr {
  given HasCanonicalRepr[String]                               = () => ""
  given list[T]: HasCanonicalRepr[List[T]]                     = () => List.empty
  given numeric[N](using num: Numeric[N]): HasCanonicalRepr[N] = () => num.zero
}
