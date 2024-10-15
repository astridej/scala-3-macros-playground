package com.astridej.macros.ex5

import com.astridej.macros.EntryPoints.*
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class TypeClassDerivationTest extends AnyFreeSpec with Matchers {
  "Can derive an Eq instance for a case class" in {
    case class Test(int: Int)
    val eq = deriveEqMacro[Test]
    eq.areEqual(Test(3), Test(4)) shouldBe false
    eq.areEqual(Test(3), Test(3)) shouldBe true
  }

  "Can also derive an Eq instance for a case class with a list" in {
    case class Test(input: List[Int], other: Boolean)
    val eq = deriveEqMacro[Test]
    eq.areEqual(Test(List(1, 2), true), Test(List(1, 3), true)) shouldBe false
    eq.areEqual(Test(List(1, 2), true), Test(List(1, 2), false)) shouldBe false
    eq.areEqual(Test(List(1, 2), true), Test(List(1, 2), true)) shouldBe true
  }

  "Can also derive an Eq instance for a case class via mirrors" in {
    case class Test(int: Int)
    val eq = MirrorDerivation.deriveEqMirrored[Test]
    eq.areEqual(Test(3), Test(4)) shouldBe false
    eq.areEqual(Test(3), Test(3)) shouldBe true

  }

  "And can derive an Eq instance for a more complex case class via mirrors" in {
    case class Test(input: List[Int], other: Boolean)
    val eq = MirrorDerivation.deriveEqMirrored[Test]
    eq.areEqual(Test(List(1, 2), true), Test(List(1, 3), true)) shouldBe false
    eq.areEqual(Test(List(1, 2), true), Test(List(1, 2), false)) shouldBe false
    eq.areEqual(Test(List(1, 2), true), Test(List(1, 2), true)) shouldBe true
  }
}
