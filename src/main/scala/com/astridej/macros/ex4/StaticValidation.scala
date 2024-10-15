package com.astridej.macros.ex4

import scala.quoted.{Expr, Quotes}

def dayMacroCode(in: Expr[Int])(using Quotes): Expr[Int] = {
  val value = in.valueOrAbort
  if (value < 1 || value > 31)
    scala.quoted.quotes.reflect.report.errorAndAbort("Day must be between 1 and 31!")
  else
    in
}