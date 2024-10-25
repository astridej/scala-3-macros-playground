package com.astridej.macros.ex2

import scala.quoted.*

object Arithmetic {
  // do addition at compile time - requires x and y to be static values
  def plusStatic(x: Expr[Int], y: Expr[Int])(using Quotes): Expr[Int] =
    Expr(x.valueOrAbort + y.valueOrAbort)

  // do addition at runtime - works with dynamic x and y
  // (but you don't actually need a macro for this, you could just write x + y...)
  def plusDynamic(x: Expr[Int], y: Expr[Int])(using Quotes): Expr[Int] = '{
    $x + $y
  }

  // testing: can we use recursive functions in macros? yes!
  // (is computing a fibonacci number this way a good idea? no.)
  def compilerFibonacci(x: Expr[Int])(using Quotes): Expr[Int] = {
    val value = x.valueOrAbort
    if (value == 0) Expr(0)
    else if (value == 1) Expr(1)
    else
      val compCode1 = compilerFibonacci(Expr(value - 1))
      val compCode2 = compilerFibonacci(Expr(value - 2))
      '{ $compCode1 + $compCode2 }
  }
}
