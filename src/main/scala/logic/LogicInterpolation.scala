package logic
import cats.syntax.all.*

import scala.quoted.*

// Example of a custom string literal that supports interpolation: let's try to parse first-order propositional logic
def propositionCode(sc: Expr[StringContext], args: Expr[Seq[Proposition]])(using Quotes): Expr[Proposition] = {
  import quoted.quotes.*
  import quotes.reflect.report
  def parsePartial(next: List[String | Expr[Proposition]]): Expr[Proposition] =
    next match {
      case Nil                              => report.errorAndAbort("Got empty statement when parsing proposition!")
      case (expr: Expr[Proposition]) :: Nil => expr
      case (expr: Expr[Proposition]) :: ("&&" | "AND") :: tail =>
        '{ Proposition.And(${ expr }, ${ parsePartial(tail) }) }
      case (expr: Expr[Proposition]) :: ("||" | "OR") :: tail =>
        '{ Proposition.Or(${ expr }, ${ parsePartial(tail) }) }
      case (expr: Expr[Proposition]) :: ("=>" | "->" | "IMPLIES") :: tail =>
        '{ Proposition.Implies(${ expr }, ${ parsePartial(tail) }) }
      case (_: Expr[Proposition]) :: tail :: tail2 =>
        report.errorAndAbort(s"Proposition followed by $tail and $tail2 ???")
      case "(" :: tail =>
        val indexOfCloser = tail
          .scanLeft(("(": String | Expr[Proposition], 1)) {
            case ((_, level), ")")   => (")", level - 1)
            case ((_, level), "(")   => ("(", level + 1)
            case ((_, level), other) => (other, level)
          }
          .indexOf(")" -> 0)
        if (indexOfCloser <= 0)
          report.errorAndAbort(s"Opening bracket without closing bracket when parsing $next.")
        else {
          val toExpr = tail.slice(0, indexOfCloser - 1)
          parsePartial(parsePartial(toExpr) :: tail.drop(indexOfCloser))
        }

      case "TRUE" :: tail                 => parsePartial('{ Proposition.True } :: tail)
      case "FALSE" :: tail                => parsePartial('{ Proposition.False } :: tail)
      case ("!" | "NOT") :: tail          => '{ Proposition.Not(${ parsePartial(tail) }) }
      case ("&&" | "AND") :: _            => report.errorAndAbort("AND statement without preceding statement!")
      case ("||" | "OR") :: _             => report.errorAndAbort("OR statement without preceding statement!")
      case ("->" | "=>" | "IMPLIES") :: _ => report.errorAndAbort("IMPLIES statement without preceding statement!")
      case ")" :: _                       => report.errorAndAbort("Closing bracket without opening bracket!")
      case (raw: String) :: tail          => parsePartial('{ Proposition.Statement(${ Expr(raw) }) } :: tail)
    }

  val context = sc.valueOrAbort
  def clean(list: List[String]): List[String] =
    list
      .flatMap( // hack to group the raw input correctly
        _.replaceAll(raw"(!|\(|\)|\)|&&|=>|->|\|\|)", " $1 ")
          .split(" ")
      )
      .map(_.trim.toUpperCase)
      .filter(_.nonEmpty)

  args match {
    case Varargs(expressions) =>
      val all: List[String | Expr[Proposition]] = context.parts.toList
        .padZipWith(expressions.toList) { case (maybeRaw, maybeExpr) =>
          val x: List[String | Expr[Proposition]] =
            clean(maybeRaw.toList) ++ maybeExpr.toList
          x
        }
        .flatten
      parsePartial(all)

    case _ =>
      parsePartial(clean(context.parts.toList))
  }
}
