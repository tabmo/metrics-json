package io.tabmo.metrics.circe

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CirceMetricsEncoderSpec extends AnyFlatSpec with Matchers {

  "test metrics" should "be well encoded" in {
    import io.circe.syntax._
    val tested = metrics.test.asJson
    val result = for {
      countCounter <- tested.hcursor.downField("metrics.test.the.counter").downField("count").as[Long]
      countTimer <- tested.hcursor.downField("metrics.test.the.timer").downField("count").as[Long]
      countMeter <- tested.hcursor.downField("metrics.test.the.meter").downField("count").as[Long]
      longGauge <- tested.hcursor.downField("metrics.test.the.gauge.long").as[Long]
      floatGauge <- tested.hcursor.downField("metrics.test.the.gauge.float").as[Float]
      stringGauge <- tested.hcursor.downField("metrics.test.the.gauge.string").as[String]
    } yield (countCounter, countTimer, countMeter, longGauge, floatGauge, stringGauge)

    result.getOrElse((0L, 0L, 0L, 0L, 0.0f, "0  ")) should be equals((16L, 0L, 1L, 42L, 42.42f, "42"))
  }
}
