package com.astridej.macros

import com.astridej.macros.ex1.*
import com.astridej.macros.ex2.*
import com.astridej.macros.ex3.*
import com.astridej.macros.ex5.*
import com.astridej.macros.ex6.*
import com.astridej.macros.ex7.*
import com.comcast.ip4s.IpAddress

object EntryPoints {

  // standard macro entry point
  inline def inspectPrintDebug(inline x: Any): Any = ${ Inspections.inspectPrintDebug('x) }

  inline def inspectReturnExpr(inline x: Any): Any = ${ Inspections.inspectReturnExpr('x) }

  inline def inspectReturnTree[T](inline x: T): String = ${ Inspections.inspectReturnTree('x) }

  inline def inspectReturnType[T](inline x: T): (String, String) = ${ Inspections.inspectReturnType('x) }

  inline def plusStatic(inline x: Int, y: Int): Int = ${ Arithmetic.plusStatic('x, 'y) }

  inline def plusDynamic(inline x: Int, y: Int): Int = ${ Arithmetic.plusDynamic('x, 'y) }

  inline def compilerFibonacci(inline n: Int): Int = ${ Arithmetic.compilerFibonacci('n) }

  inline def buildInfo(): BuildInfo = ${ buildInfoCode() }

  inline def unwiseWeatherFrog: WeatherInfo = ${ unwiseWeatherFrogCode() }

  inline def deriveEq[T]: Eq[T] = ${ deriveEqCode[T] }

  extension (inline sc: StringContext) {
    inline def dnsResolve(args: Any*): IpAddress = ${ dnsResolveCode('sc, 'args) }

    inline def binary(args: Any*): Int = ${ binaryCode('sc, 'args) }

    inline def proposition(inline args: Proposition*): Proposition =
      ${ propositionCode('sc, 'args) }
  }
}
