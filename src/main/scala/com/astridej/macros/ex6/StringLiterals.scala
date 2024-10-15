package com.astridej.macros.ex6

import cats.effect.IO
import com.comcast.ip4s.IpAddress

import java.io.File
import java.net.InetAddress
import java.nio.file.Files
import java.security.spec.{RSAPrivateKeySpec, RSAPublicKeySpec}
import java.security.{KeyFactory, PrivateKey, PublicKey}
import java.time.Instant
import scala.quoted.*

object StringLiterals {
  // use string literals to parse binary numbers via macro
  def binary(in: Expr[StringContext], args: Expr[Seq[Any]])(using Quotes): Expr[Int] = {
    val context = in.valueOrAbort.parts
    if (context.size != 1) {
      quoted.quotes.reflect.report.errorAndAbort("Interpolation not supported!")
    } else {
      Expr(Integer.parseInt(context.head, 2))
    }
  }

  // or go all-out and resolve an IP address via string literals (note: don't actually do this)
  def dnsResolve(in: Expr[StringContext], args: Expr[Seq[Any]])(using Quotes): Expr[IpAddress] = {
    val context = in.valueOrAbort.parts
    if (context.size != 1) {
      quoted.quotes.reflect.report.errorAndAbort("Interpolation not supported!")
    } else {
      val ip = InetAddress.getByName(context.head).getHostAddress
      IpAddress.fromString(ip).getOrElse(quoted.quotes.reflect.report.errorAndAbort("Problem resolving host address."))
      '{ IpAddress.fromString(${ Expr(ip) }).get }
    }
  }
}
