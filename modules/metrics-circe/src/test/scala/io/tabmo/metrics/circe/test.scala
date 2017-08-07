package metrics

import nl.grons.metrics.scala.DefaultInstrumented

object test extends DefaultInstrumented {
  metrics.counter("the.counter").inc(16)
  metrics.timer("the.timer")
  metrics.meter("the.meter").mark()
  metrics.gauge[Long]("the.gauge.long"){42l}
  metrics.gauge[Float]("the.gauge.float"){42.42f}
  metrics.gauge[String]("the.gauge.string"){"42"}
}
