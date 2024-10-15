package com.astridej.macros.ex6

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import com.astridej.macros.EntryPoints.*

class StringLiteralsTest extends AnyFreeSpec with Matchers {
  "Can parse a product ID via handwritten string literals" in {
    val productId = productId"1234567A"
    productId shouldBe ProductId("1234567A").getOrElse(fail("Invalid product ID"))
    """productId"1234"""" shouldNot compile
  }

  "Can parse a product ID via literally library" in {
    val productId = literallyProductId"1234567A"
    productId shouldBe ProductId("1234567A").getOrElse(fail("Invalid product ID"))
    """literallyProductId"1234"""" shouldNot compile
  }
}
