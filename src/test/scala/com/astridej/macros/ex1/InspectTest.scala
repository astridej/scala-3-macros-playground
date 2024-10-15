package com.astridej.macros.ex1

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import java.nio.charset.StandardCharsets
import javax.crypto.Cipher
import com.astridej.macros.EntryPoints.*

class InspectTest extends AnyFreeSpec with Matchers {
  "Inspecting code should work" in {
    case class Test(value: Int)
    inspectPrintDebug(15)
    inspectPrintDebug("Look at me I'm a string!")
    inspectPrintDebug(23 - 15)
    inspectPrintDebug {
      // chosen to be random by secure dice roll
      def random(): Int = 4
      random()
    }
    val x = 5
    inspectPrintDebug(x)
    inspectPrintDebug(Test(42))
  }

  "Inspecting code at runtime should work" in {
    case class Test(value: Int)
    inspectReturnExpr(15) shouldBe "15"
    inspectReturnExpr("Look at me I'm a string!") shouldBe "\"Look at me I\\'m a string!\""
    inspectReturnExpr(23 - 15) shouldBe "8" // scala compiler optimization??

    def indirectAddition(a: Int, b: Int): Int = a + b

    inspectReturnExpr(indirectAddition(1, 1)) shouldBe "indirectAddition(1, 1)"
    inspectReturnExpr {
      // chosen to be random by secure dice roll
      def random(): Int = 4

      random()
    } shouldBe """{
        |  def random(): scala.Int = 4
        |  random()
        |}""".stripMargin
    val x = 5
    inspectReturnExpr(x) shouldBe "x"
    inspectReturnExpr(Test(42)) shouldBe "Test.apply(42)"
  }

  "Getting types for descriptions should work" in {
    case class Test(value: Int)
    inspectReturnType(15) shouldBe ("scala.Int", "15")
    inspectReturnType("Look at me I'm a string!") shouldBe ("java.lang.String", "\"Look at me I\\'m a string!\"")
    inspectReturnType(23 - 15) shouldBe ("scala.Int", "8")
    def indirectAddition(a: Int, b: Int): Int = a + b
    inspectReturnType(indirectAddition(1, 1)) shouldBe ("scala.Int", "indirectAddition(1, 1)")
    inspectReturnType {
      // chosen to be random by secure dice roll
      def random(): Int = 4
      random()
    } shouldBe ("scala.Int",
    """{
       |  def random(): scala.Int = 4
       |  random()
       |}""".stripMargin)
    val x = 5
    inspectReturnType(x) shouldBe ("scala.Int", "x")
    inspectReturnType(Test(42)) shouldBe ("Test", "Test.apply(42)")
  }

  "Can check out syntax trees" in {
    case class Test(value: Int)
    inspectReturnTree(15) shouldBe ("Inlined(None, Nil, Literal(IntConstant(15)))")

    def indirectAddition(a: Int, b: Int): Int = a + b

    inspectReturnTree(
      indirectAddition(1, 1)
    ) shouldBe ("Inlined(None, Nil, Apply(Ident(\"indirectAddition\"), List(Literal(IntConstant(1)), Literal(IntConstant(1)))))")
    val x = 5
    inspectReturnTree(x) shouldBe ("Inlined(None, Nil, Ident(\"x\"))")
    inspectReturnTree(
      Test(42)
    ) shouldBe ("Inlined(None, Nil, Apply(Select(Ident(\"Test\"), \"apply\"), List(Literal(IntConstant(42)))))")

    inspectReturnTree((x: Test) =>
      x.value
    ) shouldBe "Inlined(None, Nil, Block(List(DefDef(\"$anonfun\", List(TermParamClause(List(ValDef(\"x\", TypeIdent(\"Test\"), None)))), Inferred(), Some(Select(Ident(\"x\"), \"value\")))), Closure(Ident(\"$anonfun\"), None)))"
  }
}
