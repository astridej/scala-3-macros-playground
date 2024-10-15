package com.astridej.macros.ex6

import org.typelevel.literally.Literally

import scala.quoted.{Expr, Quotes}
import scala.util.matching.Regex

opaque type ProductId = String

object ProductId {
  private val regex: Regex                            = "^[a-zA-Z0-9]{8}$".r
  def apply(value: String): Either[String, ProductId] = Either.cond(regex.matches(value), value, "Invalid ProductId")

  def stringLiteral(in: Expr[StringContext], args: Expr[Seq[Any]])(using Quotes): Expr[ProductId] = {
    import quoted.quotes.*
    val context = in.valueOrAbort.parts
    if (context.size != 1) {
      reflect.report.errorAndAbort("Interpolation not supported!")
    } else {
      val value = context.head
      apply(value) match {
        case Right(_)  => Expr(value)
        case Left(err) => reflect.report.errorAndAbort(err)
      }
    }
  }

  // ...or we could just use the Literally library designed for this purpose:
  object LiterallySupport extends Literally[ProductId] {
    def validate(s: String)(using Quotes): Either[String, Expr[ProductId]] = ProductId(s).map(Expr(_))
  }
}
