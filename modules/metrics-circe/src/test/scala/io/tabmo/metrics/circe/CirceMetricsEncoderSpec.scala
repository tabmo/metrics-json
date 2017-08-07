package io.tabmo.metrics.circe

import org.scalatest._

class CirceMetricsEncoderSpec extends FlatSpec with Matchers {

  "test metrics" should "be well encoded" in {
    import io.circe.syntax._
    val tested = metrics.test.asJson
    val result = for {
      countCounter <- tested.hcursor.downField("metrics.test.the.counter").downField("count").as[Long].right
      countTimer <- tested.hcursor.downField("metrics.test.the.timer").downField("count").as[Long].right
      countMeter <- tested.hcursor.downField("metrics.test.the.meter").downField("count").as[Long].right
      longGauge <- tested.hcursor.downField("metrics.test.the.gauge.long").as[Long].right
      floatGauge <- tested.hcursor.downField("metrics.test.the.gauge.float").as[Float].right
      stringGauge <- tested.hcursor.downField("metrics.test.the.gauge.string").as[String].right
    } yield (countCounter, countTimer, countMeter, longGauge, floatGauge, stringGauge)

    result.right.get should be equals((16L, 0L, 1L, 42l, 42.42f, "42"))
  }
}
