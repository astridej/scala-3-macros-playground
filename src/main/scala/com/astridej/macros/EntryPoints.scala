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
  inline def inspect(inline x: Any): Any = ${ inspectCode('x) }

  inline def inspectRuntime(inline x: Any): Any = ${ inspectCodeRuntime('x) }

  inline def inspectTreeRuntime[T](inline x: T): String = ${ inspectTreeCodeRuntime('x) }

  inline def inspectTypedRuntime[T](inline x: T): (String, String) = ${ inspectTypedCodeRuntime('x) }

  inline def plusStatic(inline x: Int, y: Int): Int = ${ plusStaticCode('x, 'y) }

  inline def plusDynamic(inline x: Int, y: Int): Int = ${ plusDynamicCode('x, 'y) }

  inline def compilerFibonacci(inline n: Int): Int = ${ compilerFibonacciCode('n) }

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
