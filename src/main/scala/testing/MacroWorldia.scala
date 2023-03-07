package testing

import scala.quoted.*

def inspectCode(x: Expr[Any])(using Quotes): Expr[Any] = {
  println(x.show)
  x
}

def inspectCodeTyped[T](x: Expr[T])(using Type[T], Quotes): Expr[T] = {
  println(Type.show[T] + ": " + x.show)
  x
}

def inspectCodeRuntime[T](x: Expr[T])(using Type[T], Quotes): Expr[(String, String)] =
  Expr((Type.show[T], x.show))

def plusStaticCode(x: Expr[Int], y: Expr[Int])(using Quotes): Expr[Int] =
  Expr(x.valueOrAbort + y.valueOrAbort)

def plusDynamicCode(x: Expr[Int], y: Expr[Int])(using Quotes): Expr[Int] = '{ ($x + $y) }

def compilerFibonacciCode(x: Expr[Int])(using Quotes): Expr[Int] = {
  val value = x.valueOrAbort
  if (value == 0) Expr(0)
  else if (value == 1) Expr(1)
  else
    val compCode1 = compilerFibonacciCode(Expr(value - 1))
    val compCode2 = compilerFibonacciCode(Expr(value - 2))
    '{ $compCode1 + $compCode2 }
}
