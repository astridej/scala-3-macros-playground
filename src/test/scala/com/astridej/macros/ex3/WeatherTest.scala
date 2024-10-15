package com.astridej.macros.ex3

import com.astridej.macros.EntryPoints.*
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class WeatherTest extends AnyFreeSpec with Matchers {
  "The unwisest frog" - {
//    YOU FOOLS
//    "can return the weather" in {
//      unwiseWeatherFrog shouldBe WeatherInfo(17, 12, 0.8d)
//    }
    "can check the weather" in {
      "unwiseWeatherFrog" shouldNot compile
    }
  }

  "We can get the compilation time and git commit" in {
    println(buildInfo())
  }
}
