package io.tabmo.metrics.circe

import scala.math.BigDecimal.RoundingMode
import com.codahale.metrics.{Counter, Gauge, Meter, MetricRegistry, Timer}
import io.tabmo.metrics.{Nano, Rate}
import io.circe.{Encoder, Json}
import io.circe.syntax._
import nl.grons.metrics.scala.DefaultInstrumented

trait CirceMetricsEncoder {

  implicit val fromNanoAsMsEncoder: Encoder[Nano] = Encoder.encodeBigDecimal.contramap(nano =>
    (BigDecimal(nano.value) / 1000000).setScale(2, RoundingMode.HALF_UP)
  )
  implicit val rateAs2DecimalEncoder: Encoder[Rate] = Encoder.encodeBigDecimal.contramap(rate =>
    BigDecimal(rate.value).setScale(2, RoundingMode.HALF_UP)
  )

  implicit val counterEncoder: Encoder[Counter] = Encoder.instance { counter =>
    Json.obj(
      "count" -> counter.getCount.asJson
    )
  }

  def encodeGauge(gauge: Gauge[_]): Json = {
    gauge.getValue match {
      case x: Int => Json.fromInt(x)
      case x: Long => Json.fromLong(x)
      case x: Float => Json.fromFloatOrNull(x)
      case x: Double => Json.fromDoubleOrNull(x)
      case x: BigDecimal => Json.fromBigDecimal(x)
      case x: BigInt => Json.fromBigInt(x)
      case x: Boolean => Json.fromBoolean(x)
      case x: String => Json.fromString(x)
      case _ => Json.fromString(gauge.getValue.toString)
    }
  }

  implicit val timerEncoder: Encoder[Timer] = Encoder.instance { timer =>
    val s = timer.getSnapshot
    Json.obj(
      "count" -> timer.getCount.asJson,
      "mean" -> Nano(s.getMean).asJson,
      "min" -> Nano(s.getMin.toDouble).asJson,
      "max" -> Nano(s.getMax.toDouble).asJson,
      "median" -> Nano(s.getMedian).asJson,
      "stddev" -> Nano(s.getStdDev).asJson,
      "p0.1" -> Nano(s.getValue(0.001)).asJson,
      "p1" -> Nano(s.getValue(0.01)).asJson,
      "p10" -> Nano(s.getValue(0.1)).asJson,
      "p25" -> Nano(s.getValue(0.25)).asJson,
      "p75" -> Nano(s.get75thPercentile()).asJson,
      "p95" -> Nano(s.get95thPercentile()).asJson,
      "p98" -> Nano(s.get98thPercentile()).asJson,
      "p99" -> Nano(s.get99thPercentile()).asJson,
      "p99.9" -> Nano(s.get999thPercentile()).asJson,
      "m1_rate" -> Rate(timer.getOneMinuteRate).asJson,
      "m5_rate" -> Rate(timer.getFiveMinuteRate).asJson,
      "m15_rate" -> Rate(timer.getFifteenMinuteRate).asJson,
      "mean_rate" -> Rate(timer.getMeanRate).asJson
    )
  }
  implicit val meterEncoder: Encoder[Meter] = Encoder.instance { meter =>
    Json.obj(
      "count" -> meter.getCount.asJson,
      "m1_rate" -> Rate(meter.getOneMinuteRate).asJson,
      "m5_rate" -> Rate(meter.getFiveMinuteRate).asJson,
      "m15_rate" -> Rate(meter.getFifteenMinuteRate).asJson,
      "mean_rate" -> Rate(meter.getMeanRate).asJson
    )
  }

  implicit val metricRegisterEncoder: Encoder[MetricRegistry] = Encoder.instance { metrics =>
    import scala.collection.JavaConverters._
    Json.obj(
      "timestamp" -> java.time.Instant.now().toString.asJson
    ).deepMerge(
      metrics.getMetrics.asScala.toSeq.sortBy(_._1).collect {
        case (s, m: Meter) => (s, m.asJson)
        case (s, t: Timer) => (s, t.asJson)
        case (s, c: Counter) => (s, c.asJson)
        case (s, g: Gauge[_]) => (s, encodeGauge(g))
      }.toMap.asJson
    )
  }

  implicit def defaultInstrumentedEncoder[M <: DefaultInstrumented]: Encoder[M] = metricRegisterEncoder.contramap(_.metricRegistry)
}
