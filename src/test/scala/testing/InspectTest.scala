package testing

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class InspectTest extends AnyFreeSpec with Matchers {
  "Inspecting code should work" in {
    case class Test(value: Int)
    inspect(15)
    inspect("Look at me I'm a string!")
    inspect(23 - 15)
    inspect {
      // chosen to be random by secure dice roll
      def random(): Int = 4
      random()
    }
    val x = 5
    inspect(x)
    inspect(Test(42))
  }

  "Inspecting code typed should work" in {
    case class Test(value: Int)
    inspectTyped(15)
    inspectTyped("Look at me I'm a string!")
    inspectTyped(23 - 15)
    inspectTyped {
      // chosen to be random by secure dice roll
      def random(): Int = 4
      random()
    }
    val x = 5
    inspectTyped(x)
    inspectTyped(Test(42))
  }

  "Pulling out descriptions into runtime should work" in {
    case class Test(value: Int)
    inspectRuntime(15) shouldBe ("scala.Int", "15")
    inspectRuntime("Look at me I'm a string!") shouldBe ("java.lang.String", "\"Look at me I\\'m a string!\"")
    inspectRuntime(23 - 15) shouldBe ("scala.Int", "8") // scala compiler optimization??
    inspectRuntime {
      // chosen to be random by secure dice roll
      def random(): Int = 4
      random()
    } shouldBe ("scala.Int", """{
                                   |  def random(): scala.Int = 4
                                   |  random()
                                   |}""".stripMargin)
    val x = 5
    inspectRuntime(x) shouldBe ("scala.Int", "x")
    inspectRuntime(Test(42)) shouldBe ("Test", "Test.apply(42)")
  }

  "Static plus" - {
    "should calculate the value at compile time" in {
      inspectRuntime(plusStatic(1, 1)) shouldBe ("scala.Int", "(2: scala.Int)")
    }
    "should work for static values" in {
      plusStatic(10, 15) shouldBe 25
      plusStatic(10, 1) shouldBe 11
      plusStatic(-1, 1) shouldBe 0
    }

    "should fall over and die for dynamic values" in {
      val x = 5
      "plusStatic(1, 1)" should compile
      "plusStatic(x, 1)" shouldNot compile
    }
  }

  "Dynamic plus" - {
    "should work for static values" in {
      plusDynamic(10, 15) shouldBe 25
      plusDynamic(10, 1) shouldBe 11
      plusDynamic(-1, 1) shouldBe 0
    }

    "should fall over and die for dynamic values" in {
      val x = 5
      plusDynamic(x, 1) shouldBe 6
    }
  }

  "Compiler should be able to compute Fibonacci numbers" in {
    compilerFibonacci(7) shouldBe 13
    inspectRuntime(compilerFibonacci(10))._2 shouldBe "(55: scala.Int)"
  }
}
