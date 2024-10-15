package com.astridej.macros.ex2

import com.astridej.macros.EntryPoints.*
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class ArithmeticTest extends AnyFreeSpec with Matchers {

  "Static plus" - {
    "should calculate the value at compile time" in {
      inspectReturnType(plusStatic(1, 1)) shouldBe ("scala.Int", "(2: scala.Int)")
    }
    "should work for static values" in {
      plusStatic(10, 15) shouldBe 25
      plusStatic(10, 1) shouldBe 11
      plusStatic(-1, 1) shouldBe 0
    }

    "should fall over and die for dynamic values" in {
      val x = 5
      // plusStatic(x, 1) shouldBe 6
      "plusStatic(x, 1)" shouldNot compile
    }
  }

  "Dynamic plus" - {
    "should work for static values" in {
      plusDynamic(10, 15) shouldBe 25
      plusDynamic(10, 1) shouldBe 11
      plusDynamic(-1, 1) shouldBe 0
    }

    "should work just fine for dynamic values" in {
      val x = 5
      plusDynamic(x, 1) shouldBe 6
    }
  }

  "Compiler should be able to compute Fibonacci numbers" in {
    compilerFibonacci(7) shouldBe 13
    inspectReturnExpr(compilerFibonacci(10)) shouldBe "(55: scala.Int)"
  }
}
