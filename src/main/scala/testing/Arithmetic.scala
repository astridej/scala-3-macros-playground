package testing

import scala.quoted.*

// do addition at compile time - requires x and y to be static values
def plusStaticCode(x: Expr[Int], y: Expr[Int])(using Quotes): Expr[Int] =
  Expr(x.valueOrAbort + y.valueOrAbort)

// do addition at runtime - works with dynamic x and y
// (but you don't actually need a macro for this, you could just write x + y...)
def plusDynamicCode(x: Expr[Int], y: Expr[Int])(using Quotes): Expr[Int] = '{ $x + $y }

// testing: can we use recursive functions in macros? yes!
// (is computing a fibonacci number this way a good idea? no.)
def compilerFibonacciCode(x: Expr[Int])(using Quotes): Expr[Int] = {
  val value = x.valueOrAbort
  if (value == 0) Expr(0)
  else if (value == 1) Expr(1)
  else
    val compCode1 = compilerFibonacciCode(Expr(value - 1))
    val compCode2 = compilerFibonacciCode(Expr(value - 2))
    '{ $compCode1 + $compCode2 }
}
