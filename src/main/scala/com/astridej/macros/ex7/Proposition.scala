package com.astridej.macros.ex7

import cats.syntax.all.*

sealed trait Proposition {
  def truthValue(atoms: Map[String, Boolean]): Option[Boolean]
}

object Proposition {

  case object True extends Proposition {
    override def truthValue(atoms: Map[String, Boolean]): Option[Boolean] = Some(true)
  }

  case object False extends Proposition {
    override def truthValue(atoms: Map[String, Boolean]): Option[Boolean] = Some(false)
  }

  case class Statement(name: String) extends Proposition {
    override def truthValue(atoms: Map[String, Boolean]): Option[Boolean] = atoms.get(name)
  }

  case class And(left: Proposition, right: Proposition) extends Proposition {
    override def truthValue(atoms: Map[String, Boolean]): Option[Boolean] =
      (left.truthValue(atoms), right.truthValue(atoms)).mapN(_ && _)
  }

  case class Or(left: Proposition, right: Proposition) extends Proposition {
    override def truthValue(atoms: Map[String, Boolean]): Option[Boolean] =
      (left.truthValue(atoms), right.truthValue(atoms)).mapN(_ || _)
  }

  case class Not(negated: Proposition) extends Proposition {
    override def truthValue(atoms: Map[String, Boolean]): Option[Boolean] = negated.truthValue(atoms).map(!_)
  }

  case class Implies(a: Proposition, b: Proposition) extends Proposition {
    override def truthValue(atoms: Map[String, Boolean]): Option[Boolean] =
      (a.truthValue(atoms), b.truthValue(atoms)) match {
        case (Some(false), _)   => Some(true)
        case (_, Some(boolean)) => Some(boolean)
        case (_, None)          => None
      }
  }
}
