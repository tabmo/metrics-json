# metrics-json
expose metrics in json

## Requirement

* Add repository

```
"Tabmo Bintray" at "https://dl.bintray.com/tabmo/maven"
```

* Use scala 2.11 or 2.12

* Add dependencies

```scala
val metricsCirceVersion = ...
libraryDependencies += "io.tabmo" %% "metrics-circe" % metricsCirceVersion

// Or use playjson

val metricsPlayjsonVersion = ...
libraryDependencies += "io.tabmo" %% "metrics-playjson" % metricsPlayjsonVersion
```

## metrics-circe

|Circe version   | metrics-circe version  |
|----------------|------------------------|
| 0.8.0          |  0.1                   |

### Example with play 2.6

* You need to add dependencies for `play-circe`

```scala
libraryDependencies += "play-circe" %% "play-circe" % s"2.6-$playCirceVersion"
```

```scala
package play.api.libs.circe

import io.circe.generic.auto._
import io.circe.syntax._
import play.api._
import play.api.mvc._
import nl.grons.metrics.scala.DefaultInstrumented

class MetricController(val controllerComponents: ControllerComponents) extends BaseController with DefaultInstrumented with Circe {

  import io.circe.syntax._
  import io.tabmo.metrics.circe._
  
  def get = Action {
    Metrics.callToApiCounter.inc()
    Ok()
  }

  //expose all metrics to our json api
  def exposeMetrics = Action {
    Ok(Metrics.asJson)
  }
}

object Metrics extends DefaultInstrumented {
  val callToApiCounter = metrics.counter("callToApiCounter")
}
```

## metrics-playjson

|Play json version   | metrics-playjson version  |
|--------------------|---------------------------|
| 2.6.2              |  0.1                      |

### Example with play 2.6

```scala
package play.api.libs.circe

import io.circe.generic.auto._
import io.circe.syntax._
import play.api._
import play.api.mvc._
import nl.grons.metrics.scala.DefaultInstrumented

class MetricController(val controllerComponents: ControllerComponents) extends BaseController with DefaultInstrumented {

  import play.api.libs.json.Writes
  import io.tabmo.metrics.playjson._
  
  def get = Action {
    Metrics.callToApiCounter.inc()
    Ok()
  }

  //expose all metrics to our json api
  def exposeMetrics = Action {
    Ok(Writes.of[DefaultInstrumented].writes(Metrics))
  }
}

object Metrics extends DefaultInstrumented {
  val callToApiCounter = metrics.counter("callToApiCounter")
}
```