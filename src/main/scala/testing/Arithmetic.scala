package testing

import scala.quoted.*

def plusStaticCode(x: Expr[Int], y: Expr[Int])(using Quotes): Expr[Int] =
  Expr(x.valueOrAbort + y.valueOrAbort)

def plusDynamicCode(x: Expr[Int], y: Expr[Int])(using Quotes): Expr[Int] = '{ $x + $y }

def compilerFibonacciCode(x: Expr[Int])(using Quotes): Expr[Int] = {
  val value = x.valueOrAbort
  if (value == 0) Expr(0)
  else if (value == 1) Expr(1)
  else
    val compCode1 = compilerFibonacciCode(Expr(value - 1))
    val compCode2 = compilerFibonacciCode(Expr(value - 2))
    '{ $compCode1 + $compCode2 }
}
