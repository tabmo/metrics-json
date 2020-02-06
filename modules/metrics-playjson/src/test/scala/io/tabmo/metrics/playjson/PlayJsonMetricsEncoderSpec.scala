package io.tabmo.metrics.playjson

import play.api.libs.json.Writes
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import nl.grons.metrics4.scala.DefaultInstrumented

class PlayJsonMetricsEncoderSpec extends AnyFlatSpec with Matchers {

  "test metrics" should "be well encoded" in {
    val tested = Writes.of[DefaultInstrumented].writes(metrics.test)

    (tested \ "metrics.test.the.counter" \ "count").as[Long] shouldEqual 16L
    (tested \ "metrics.test.the.timer" \ "count").as[Long] shouldEqual 0L
    (tested \ "metrics.test.the.meter" \ "count").as[Long] shouldEqual 1L

    (tested \ "metrics.test.the.gauge.long").as[Long] shouldEqual 42L
    (tested \ "metrics.test.the.gauge.float").as[BigDecimal] shouldEqual BigDecimal(42.42)
    (tested \ "metrics.test.the.gauge.string").as[String] shouldEqual "42"
  }
}
