package testing

import scala.quoted.*

def inspectCode(x: Expr[Any])(using Quotes): Expr[Any] = {
  println(x.show)
  x
}

def inspectCodeRuntime(x: Expr[Any])(using Quotes): Expr[String] = Expr(x.show)

def inspectTypedCodeRuntime[T](x: Expr[T])(using Type[T], Quotes): Expr[(String, String)] =
  Expr((Type.show[T], x.show))

def inspectTreeCodeRuntime[T](x: Expr[T])(using Type[T], Quotes): Expr[String] = {
  import quotes.reflect.*
  Expr(x.asTerm.show(using Printer.TreeStructure))
}
