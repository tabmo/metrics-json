# metrics-json

Expose Dropwizard metrics in json

Compatible with scala 2.11 and scala 2.12, with play-json and circe.

## Installation

### Select `metrics-json` version

*Circe*

|Circe version   | metrics-circe version  |
|----------------|------------------------|
| 0.8.0          |  0.1                   |
| 0.9.A          |  0.3                   |
| 0.12.3         |  1.0                   |
| 0.13.0         |  1.1                   |

*Play-json*

|Play json version   | metrics-playjson version  |
|--------------------|---------------------------|
| 2.6.2              |  0.1                      |
| 2.6.7              |  0.3                      |
| 2.7.4              |  1.1                      |
| 2.8.1              |  1.2.1                    |

### Add Dependency

```
resolvers += "Tabmo MyGet Public" at "https://www.myget.org/F/tabmo-public/maven/"
```

```scala
// For Circe
libraryDependencies += "io.tabmo" %% "metrics-circe" % metricsCirceVersion

// For Play-json
libraryDependencies += "io.tabmo" %% "metrics-playjson" % metricsPlayjsonVersion
```

## Usage

### Circe (eg with Play 2.6)

Required dependency: `play-circe`

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

### Play-json (eg with Play 2.6)


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
