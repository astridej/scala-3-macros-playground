package testing

import java.security.{PrivateKey, PublicKey}
import java.time.Instant

inline def inspect(inline x: Any): Any     = ${ inspectCode('x) }
inline def inspectTyped[T](inline x: T): T = ${ inspectCodeTyped('x) }

inline def inspectTreeRuntime[T](inline x: T): String       = ${ inspectTreeCodeRuntime('x) }
inline def inspectRuntime[T](inline x: T): (String, String) = ${ inspectCodeRuntime('x) }

inline def plusStatic(inline x: Int, y: Int): Int  = ${ plusStaticCode('x, 'y) }
inline def plusDynamic(inline x: Int, y: Int): Int = ${ plusDynamicCode('x, 'y) }

inline def compilerFibonacci(inline n: Int): Int = ${ compilerFibonacciCode('n) }

inline def buildInfo(): BuildInfo = ${ buildInfoCode() }

inline def inspectTypeRepr[T](): String = ${ inspectTypeReprCode[T] }
//inline def genRsa(in: String): (PrivateKey, PublicKey) = ${ genRsaCode('in) }

inline def unwiseWeatherFrog: WeatherInfo = ${ unwiseWeatherFrogCode() }

inline def deriveEq[T]: Eq[T] = ${ deriveEqCode[T] }
