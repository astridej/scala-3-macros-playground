package testing

import cats.effect.IO
import com.comcast.ip4s.IpAddress
import dotty.tools.dotc.semanticdb.SymbolInformation.Kind.PACKAGE

import java.io.File
import java.net.InetAddress
import java.nio.file.Files
import java.security.{KeyFactory, PrivateKey, PublicKey}
import java.security.spec.{RSAPrivateKeySpec, RSAPublicKeySpec}
import java.time.Instant
import scala.quoted.*

def binaryCode(in: Expr[StringContext], args: Expr[Seq[Any]])(using Quotes): Expr[Int] = {
  val context = in.valueOrAbort.parts
  if (context.size != 1) {
    quoted.quotes.reflect.report.errorAndAbort("Interpolation not supported!")
  } else {
    Expr(Integer.parseInt(context.head, 2))
  }
}

def dnsResolveCode(in: Expr[StringContext], args: Expr[Seq[Any]])(using Quotes): Expr[IpAddress] = {
  val context = in.valueOrAbort.parts
  if (context.size != 1) {
    quoted.quotes.reflect.report.errorAndAbort("Interpolation not supported!")
  } else {
    val ip = InetAddress.getByName(context.head).getHostAddress
    IpAddress.fromString(ip).getOrElse(quoted.quotes.reflect.report.errorAndAbort("Problem resolving host address."))
    '{ IpAddress.fromString(${ Expr(ip) }).get }
  }
}
