package com.astridej.macros.ex5

import scala.annotation.tailrec
import scala.compiletime.summonInline
import scala.deriving.Mirror
import scala.quoted.*

trait Eq[-C] {
  def areEqual(c: C, d: C): Boolean
}

object Eq {
  given numeric[N](using num: Numeric[N]): Eq[N] = (c, d) => num.equiv(c, d)
  given Eq[String]                               = (c, d) => c == d
  given Eq[Boolean]                              = (c, d) => c == d

  given coll[C](using underlying: Eq[C]): Eq[Iterable[C]] = new Eq[Iterable[C]] {
    @tailrec
    override def areEqual(c: Iterable[C], d: Iterable[C]): Boolean = (c.headOption, d.headOption) match {
      case (None, None)                                                    => true
      case (Some(chead), Some(dhead)) if underlying.areEqual(chead, dhead) => this.areEqual(c.tail, d.tail)
      case _                                                               => false
    }
  }
}

// We can use macros to try to derive an Eq instance for a case class
// this requires dipping into the reflect API and creating some expressions via the actual syntax trees, which is pretty ugly
def deriveEqCode[T: Type](using Quotes): Expr[Eq[T]] = {
  import quotes.reflect.*
  val sym = TypeRepr.of[T].typeSymbol
  if (!sym.isClassDef || !sym.flags.is(Flags.Case))
    quotes.reflect.report.errorAndAbort("Not a case class.")
  val fieldCheckEqs: List[(Expr[T], Expr[T]) => Expr[Boolean]] = sym.caseFields.map { field =>
    val fieldType = TypeRepr.of[T].memberType(field)
    fieldType.asType match {
      case '[t] =>
        val eq =
          Expr.summon[Eq[t]].getOrElse(quotes.reflect.report.errorAndAbort(s"Could not find implicit for field $field"))
        (s: Expr[T], t: Expr[T]) =>
          '{ ${ eq }.areEqual(${ Select(s.asTerm, field).asExprOf[t] }, ${ Select(t.asTerm, field).asExprOf[t] }) }
    }
  }
  '{
    new Eq[T] {
      override def areEqual(c: T, d: T): Boolean = ${
        fieldCheckEqs
          .map { fn =>
            fn('c, 'd)
          }
          .foldRight('true)((a, b) => '{ $a && $b })
      }
    }
  }
}

// We can also use inline and Mirror to derive Eq instances
// Significantly simpler and more elegant, and can be shortened even more via Shapeless
object MirrorDerivation {
  given Eq[EmptyTuple.type] = (_: EmptyTuple.type, _: EmptyTuple.type) => true

  given inductionEq[S, T <: Tuple](using sEq: Eq[S], tEq: Eq[T]): Eq[S *: T] = (c: S *: T, d: S *: T) =>
    (c, d) match {
      case (s1 *: t1, s2 *: t2) => sEq.areEqual(s1, s2) && tEq.areEqual(t1, t2)
    }

  inline def deriveEqMirrored[T <: Product](
      using mirror: Mirror.ProductOf[T]
  ): Eq[T] = {
    import MirrorDerivation.given
    val derivedEq = summonInline[Eq[mirror.MirroredElemTypes]]
    (s: T, t: T) => derivedEq.areEqual(Tuple.fromProductTyped(s), Tuple.fromProductTyped(t))
  }
}
