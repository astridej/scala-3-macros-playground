package testing

import scala.compiletime.{erasedValue, summonInline}
import scala.deriving.Mirror

object MirrorUniverse {
  given Eq[EmptyTuple.type] = (_: EmptyTuple.type, _: EmptyTuple.type) => true

  given inductionEq[S, T <: Tuple](using sEq: Eq[S], tEq: Eq[T]): Eq[S *: T] = (c: S *: T, d: S *: T) =>
    (c, d) match {
      case (s1 *: t1, s2 *: t2) => sEq.areEqual(s1, s2) && tEq.areEqual(t1, t2)
    }

  inline def deriveEqMirrored[T <: Product](
      using mirror: Mirror.ProductOf[T]
  ): Eq[T] = {
    import testing.MirrorUniverse.given
    val derivedEq = summonInline[Eq[mirror.MirroredElemTypes]]
    (s: T, t: T) => derivedEq.areEqual(Tuple.fromProductTyped(s), Tuple.fromProductTyped(t))
  }

}
