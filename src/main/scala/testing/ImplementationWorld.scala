package testing

import com.comcast.ip4s.IpAddress

import java.security.{PrivateKey, PublicKey}
import java.time.Instant
import scala.util.Try

inline def inspect(inline x: Any): Any        = ${ inspectCode('x) }
inline def inspectRuntime(inline x: Any): Any = ${ inspectCodeRuntime('x) }

inline def inspectTreeRuntime[T](inline x: T): String            = ${ inspectTreeCodeRuntime('x) }
inline def inspectTypedRuntime[T](inline x: T): (String, String) = ${ inspectTypedCodeRuntime('x) }

inline def plusStatic(inline x: Int, y: Int): Int  = ${ plusStaticCode('x, 'y) }
inline def plusDynamic(inline x: Int, y: Int): Int = ${ plusDynamicCode('x, 'y) }

inline def compilerFibonacci(inline n: Int): Int = ${ compilerFibonacciCode('n) }

inline def buildInfo(): BuildInfo = ${ buildInfoCode() }

inline def unwiseWeatherFrog: WeatherInfo = ${ unwiseWeatherFrogCode() }

inline def deriveEq[T]: Eq[T] = ${ deriveEqCode[T] }

extension (inline sc: StringContext) {
  inline def dnsResolve(args: Any*): IpAddress = ${ dnsResolveCode('sc, 'args) }

  inline def binary(args: Any*): Int = ${ binaryCode('sc, 'args) }
}
