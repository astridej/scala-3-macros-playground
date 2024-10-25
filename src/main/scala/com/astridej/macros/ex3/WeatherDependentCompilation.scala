package com.astridej.macros.ex3

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import org.http4s.ember.client.EmberClientBuilder
import org.typelevel.log4cats.LoggerFactory

import java.util.TimeZone
import scala.quoted.{Expr, Quotes, ToExpr}

// you can run pretty much any code during compilation, and cause custom errors
// i.e. you COULD do the following (but really shouldn't)
object WeatherDependentCompilation {

  // ToExpr typeclass allows "transferring" instances of types between compile-time and runtime
  // by explaining how to construct an expression that will recreate the value
  given ToExpr[WeatherInfo] = new ToExpr[WeatherInfo] {
    override def apply(x: WeatherInfo)(using Quotes): Expr[WeatherInfo] = '{
      WeatherInfo(${ Expr(x.high) }, ${ Expr(x.low) }, ${ Expr(x.rainChance) })
    }
  }

  given LoggerFactory[IO] = org.typelevel.log4cats.noop.NoOpFactory[IO]

  def unwiseWeatherFrog()(using quotes: Quotes): Expr[WeatherInfo] = {
    val weatherInfo = EmberClientBuilder
      .default[IO]
      .build
      .use { client =>
        WeatherFrog
          .buildOpenMeteo[IO](client)
          // Fort William in Scotland, to make absolutely sure this goes terribly wrong
          .getTodaysWeather("56.82", "-5.1", TimeZone.getTimeZone("Europe/London"))
      // Berlin is TOO WARM AND SUNNY what a world
      // .getTodaysWeather("52.52", "13.41", TimeZone.getTimeZone("Europe/Berlin"))
      }
      .unsafeRunSync() // usually I would say unsafeRunSync is a sign you are doing something wrong, but here there are so many other problems with what we're doing already

    weatherInfo.complaint match {
      case Nil =>
        println("The weather is good, the compiler can continue working!")
        Expr(weatherInfo)
      case nonempty =>
        quotes.reflect.report.errorAndAbort(
          s"The weather is bad because: ${nonempty.mkString("\n* ", "\n* ", "\n")}So the compiler is sulking."
        )
    }
  }
}
