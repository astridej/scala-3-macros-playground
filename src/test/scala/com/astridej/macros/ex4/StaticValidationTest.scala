package com.astridej.macros.ex4

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class StaticValidationTest extends AnyFreeSpec with Matchers {
  "Macro" - {
    "Can create a valid day" in {
      Day.applyMacro(25)
    }

    "Will refuse to compile an invalid day" in {
      // Day.applyMacro(32)
      "Day.applyMacro(32)" shouldNot compile
    }
  }

  "Inline" - {
    "Can create a valid day" in {
      Day.applyInline(25)
    }

    "Will refuse to compile an invalid day" in {
      // Day.applyInline(32)
      "Day.applyInline(32)" shouldNot compile
    }
  }
}
