package com.astridej.macros.ex3

import java.io.File
import java.time.Instant
import scala.quoted.{Expr, Quotes, ToExpr}

case class BuildInfo(time: Instant, gitCommit: String)

// a significantly more reasonable example: you could extract build time and git commit hash during compilation
object BuildInfo {
  given ToExpr[BuildInfo] = new ToExpr[BuildInfo] {
    override def apply(x: BuildInfo)(using Quotes): Expr[BuildInfo] = '{
      BuildInfo(
        Instant.ofEpochSecond(${ Expr(x.time.getEpochSecond) }, ${ Expr(x.time.getNano) }),
        ${ Expr(x.gitCommit) }
      )
    }
  }

  def buildInfo()(using quotes: Quotes): Expr[BuildInfo] = {
    import sys.process.*
    val now                 = Instant.now()
    val currentFileLocation = quotes.reflect.Position.ofMacroExpansion.sourceFile.jpath
    val process = Process("git rev-parse HEAD", new File(currentFileLocation.getParent().toAbsolutePath().toString))
    Expr(BuildInfo(now, process.!!))
  }
}
