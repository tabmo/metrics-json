package io.tabmo.metrics.playjson

import scala.math.BigDecimal.RoundingMode

import com.codahale.metrics._
import io.tabmo.metrics.{Nano, Rate}

import nl.grons.metrics4.scala.DefaultInstrumented

trait PlayJsonMetricsEncoder {

  import play.api.libs.json.Writes._
  import play.api.libs.json._

  implicit val locationReads: Writes[Nano] = Writes(nano =>
    JsNumber((BigDecimal(nano.value) / 1000000).setScale(2, RoundingMode.HALF_UP))
  )

  implicit val rateAs2DecimalEncoder: Writes[Rate] = Writes(rate =>
    JsNumber(BigDecimal(rate.value).setScale(2, RoundingMode.HALF_UP))
  )

  implicit val counterEncoder: Writes[Counter] = Writes(counter =>
    Json.obj(
      "count" -> counter.getCount()
    )
  )

  implicit val timerEncoder: Writes[Timer] = Writes[Timer] { timer =>
    val s = timer.getSnapshot
    Json.obj(
      "count" -> timer.getCount,
      "mean" -> Nano(s.getMean),
      "min" -> Nano(s.getMin.toDouble),
      "max" -> Nano(s.getMax.toDouble),
      "median" -> Nano(s.getMedian),
      "stddev" -> Nano(s.getStdDev),
      "p0.1" -> Nano(s.getValue(0.001)),
      "p1" -> Nano(s.getValue(0.01)),
      "p10" -> Nano(s.getValue(0.1)),
      "p25" -> Nano(s.getValue(0.25)),
      "p75" -> Nano(s.get75thPercentile()),
      "p95" -> Nano(s.get95thPercentile()),
      "p98" -> Nano(s.get98thPercentile()),
      "p99" -> Nano(s.get99thPercentile()),
      "p99.9" -> Nano(s.get999thPercentile()),
      "m1_rate" -> Rate(timer.getOneMinuteRate),
      "m5_rate" -> Rate(timer.getFiveMinuteRate),
      "m15_rate" -> Rate(timer.getFifteenMinuteRate),
      "mean_rate" -> Rate(timer.getMeanRate)
    )
  }

  implicit val meterEncoder: Writes[Meter] = Writes { meter =>
    Json.obj(
      "count" -> meter.getCount,
      "m1_rate" -> Rate(meter.getOneMinuteRate),
      "m5_rate" -> Rate(meter.getFiveMinuteRate),
      "m15_rate" -> Rate(meter.getFifteenMinuteRate),
      "mean_rate" -> Rate(meter.getMeanRate)
    )
  }

  def gaugeEncoder(gauge: Gauge[_]): JsValue = {
    gauge.getValue match {
      case x: Int => JsNumber(x)
      case x: Long => JsNumber(x)
      case x: Float => JsNumber(x.toDouble)
      case x: Double => JsNumber(x)
      case x: BigDecimal => JsNumber(x)
      case x: BigInt => JsNumber(BigDecimal(x))
      case x: Boolean => JsBoolean(x)
      case x: String => JsString(x)
      case _ => JsString(gauge.getValue.toString)
    }
  }

  implicit val metricRegisterEncoder: Writes[MetricRegistry] = Writes[MetricRegistry] { metrics =>
    def encode(key: String, metric: Metric) = metric match {
      case m: Meter => Json.obj(key -> m)
      case t: Timer => Json.obj(key -> t)
      case c: Counter => Json.obj(key -> c)
      case g: Gauge[_] => Json.obj(key -> gaugeEncoder(g))
      case _ => Json.obj()
    }

    import scala.jdk.CollectionConverters._
    metrics.getMetrics.asScala.toSeq.sortBy(_._1).foldLeft(Json.obj("timestamp" -> java.time.Instant.now().toString)) { (json, e) =>
      json ++ encode(e._1, e._2)
    }
  }

  implicit def defaultInstrumentedEncoder[M <: DefaultInstrumented]: Writes[M] = Writes.contravariantfunctorWrites.contramap(metricRegisterEncoder, _.metricRegistry)
}
