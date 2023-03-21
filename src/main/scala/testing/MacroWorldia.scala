package testing

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import dotty.tools.dotc.semanticdb.SymbolInformation.Kind.PACKAGE
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.circe.CirceInstances.*
import org.http4s.circe.CirceEntityDecoder.*

import java.io.File
import java.nio.file.Files
import java.security.{KeyFactory, PrivateKey, PublicKey}
import java.security.spec.{RSAPrivateKeySpec, RSAPublicKeySpec}
import java.time.Instant
import scala.quoted.*

def inspectCode(x: Expr[Any])(using Quotes): Expr[Any] = {
  println(x.show)
  x
}

def inspectCodeTyped[T](x: Expr[T])(using Type[T], Quotes): Expr[T] = {
  println(Type.show[T] + ": " + x.show)
  x
}

def inspectCodeRuntime[T](x: Expr[T])(using Type[T], Quotes): Expr[(String, String)] =
  Expr((Type.show[T], x.show))

def inspectTreeCodeRuntime[T](x: Expr[T])(using Type[T], Quotes): Expr[String] = {
  import quotes.reflect.*
  Expr(x.asTerm.show(using Printer.TreeStructure))
}

def plusStaticCode(x: Expr[Int], y: Expr[Int])(using Quotes): Expr[Int] =
  Expr(x.valueOrAbort + y.valueOrAbort)

def plusDynamicCode(x: Expr[Int], y: Expr[Int])(using Quotes): Expr[Int] = '{ ($x + $y) }

def compilerFibonacciCode(x: Expr[Int])(using Quotes): Expr[Int] = {
  val value = x.valueOrAbort
  if (value == 0) Expr(0)
  else if (value == 1) Expr(1)
  else
    val compCode1 = compilerFibonacciCode(Expr(value - 1))
    val compCode2 = compilerFibonacciCode(Expr(value - 2))
    '{ $compCode1 + $compCode2 }
}

def buildInfoCode()(using quotes: Quotes): Expr[BuildInfo] = {
  import sys.process.*
  val now                 = Instant.now()
  val currentFileLocation = quotes.reflect.Position.ofMacroExpansion.sourceFile.jpath
  val process = Process("git rev-parse HEAD", new File(currentFileLocation.getParent().toAbsolutePath().toString))
  Expr(BuildInfo(now, process.!!))
}

def unwiseWeatherFrogCode()(using quotes: Quotes): Expr[WeatherInfo] = {
  // 🐸
  val weatherInfo =
    EmberClientBuilder
      .default[IO]
      .build
      .use { client =>
        client
          .expect[OpenMeteoResponse](
            "https://api.open-meteo.com/v1/forecast?latitude=52.52&longitude=13.41&daily=weathercode,temperature_2m_max,temperature_2m_min,precipitation_probability_max&timezone=Europe%2FBerlin"
          )
          .map(_.toWeatherInfo)
      }
      .unsafeRunSync()
  weatherInfo.complaint match {
    case Some(complaint) =>
      quotes.reflect.report.errorAndAbort(s"The weather is bad because $complaint, so the compiler is sulking.")
    case None =>
      println("The weather is good, the compiler can continue working!")
      Expr(weatherInfo)
  }
}

//def genRsaCode(textExpr: Expr[String])(
//    using quotes: Quotes
//): Expr[(PrivateKey, PublicKey)] = {
//  import quotes.reflect.report.errorAndAbort
//  val (rawPrimeOne, rawPrimeTwo, rawPublicExp) = textExpr.valueOrAbort.split(":").toList match {
//    case rawOne :: rawTwo :: rawPub :: Nil => (rawOne, rawTwo, rawPub)
//    case _ =>
//      errorAndAbort(
//        s"Invalid format for RSA private key! Expected two primes and public exponent in that order",
//        textExpr
//      )
//  }
//  val primeOne       = BigInt(rawPrimeOne, 10)
//  val primeTwo       = BigInt(rawPrimeTwo, 10)
//  val publicExponent = BigInt(rawPublicExp)
//  // sanity check
//  if (!primeOne.isProbablePrime(5))
//    errorAndAbort("First provided number is not prime!", textExpr)
//  if (!primeTwo.isProbablePrime(5))
//    errorAndAbort("Second provided number is not prime!", textExpr)
//
//  val modulus = primeOne * primeTwo
//  val phi     = (primeOne - 1) * (primeTwo - 1)
//
//  if (phi.mod(publicExponent) == 0)
//    errorAndAbort("Public exponent divides phi, no private exponent can exist.", textExpr)
//  val privateExponent = phi.modInverse(publicExponent)
//
//  '{
//    val privateKeySpec = new RSAPrivateKeySpec(${ Expr(modulus) }.bigInteger, ${ Expr(privateExponent) }.bigInteger)
//    val publicKeySpec  = new RSAPublicKeySpec(${ Expr(modulus) }.bigInteger, ${ Expr(publicExponent) }.bigInteger)
//    val keyFactory     = KeyFactory.getInstance("RSA")
//    val privateKey     = keyFactory.generatePrivate(privateKeySpec)
//    val publicKey      = keyFactory.generatePublic(publicKeySpec)
//    (privateKey, publicKey)
//  }
//}

def inspectTypeReprCode[T: Type](using Quotes): Expr[String] = {
  import quotes.reflect.*
  val repr = TypeRepr.of[T]
  Expr(repr.show)
}

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
