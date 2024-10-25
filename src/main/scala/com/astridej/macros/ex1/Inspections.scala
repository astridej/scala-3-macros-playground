package com.astridej.macros.ex1

import scala.quoted.*

object Inspections {
  def inspectPrintDebug(x: Expr[Any])(
      using Quotes
  ): Expr[Any] = {
    println(x.show)
    x
  }

  def inspectReturnExpr(x: Expr[Any])(using Quotes): Expr[String] = Expr(x.show)

  def inspectReturnType[T](
      x: Expr[T]
  )(using Type[T], Quotes): Expr[(String, String)] =
    Expr((Type.show[T], x.show))

  def inspectReturnTree[T](x: Expr[T])(using Type[T], Quotes): Expr[String] = {
    import quotes.reflect.*
    Expr(x.asTerm.show(using Printer.TreeStructure))
  }
}
