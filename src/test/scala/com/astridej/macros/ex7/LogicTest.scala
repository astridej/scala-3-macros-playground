package com.astridej.macros.ex7

import com.astridej.macros.EntryPoints.*
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers.*

class LogicTest extends AnyFreeSpec {
  import Proposition.*
  "Can parse atomic statements" in {
    proposition"true" shouldBe True
    proposition"A" shouldBe Statement("A")
  }

  "Can parse binary operations" in {
    proposition"A && B" shouldBe And(Statement("A"), Statement("B"))
    proposition"false or C" shouldBe Or(False, Statement("C"))
    proposition"false=>everything" shouldBe Implies(False, Statement("EVERYTHING"))
  }

  "Can parse not" in {
    proposition"!X" shouldBe Not(Statement("X"))
    proposition"!!false" shouldBe Not(Not(False))
    proposition"not true" shouldBe Not(True)
  }

  "Can parse nested statements" in {
    proposition"(A && B) => C" shouldBe Implies(And(Statement("A"), Statement("B")), Statement("C"))
    proposition"true && (false || (false || true))" shouldBe And(
      True,
      Or(False, Or(False, True))
    )
  }

  "Can interpolate statements" in {
    val a = Statement("A")
    proposition"$a" shouldBe a

    val x = Statement("X")
    val y = Statement("Y")
    proposition"($x && $y) => C" shouldBe Implies(And(x, y), Statement("C"))

    val implication = proposition"X => Y"
    proposition"$implication => true" shouldBe Implies(Implies(Statement("X"), Statement("Y")), True)
  }

  "Will error when attempting to interpolate something that is not a proposition" in {
    val x = 5
    "proposition\"$x\"" shouldNot compile
  }

}
