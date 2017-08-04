package metrics

import nl.grons.metrics.scala.DefaultInstrumented

object test extends DefaultInstrumented {
  metrics.counter("the.counter").inc(16)
  metrics.timer("the.timer")
  metrics.meter("the.meter").mark()
}
