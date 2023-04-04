package testing

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class InterpolationExampleTest extends AnyFreeSpec with Matchers {
  "Can parse a number as binary via string interpolation" in {
    binary"10001" shouldBe 17
  }

  "Can do a DNS lookup at compile time via string interpolation, because why not" in {
    val address = dnsResolve"google.com"
    print(address.asIpv4)
  }

}
