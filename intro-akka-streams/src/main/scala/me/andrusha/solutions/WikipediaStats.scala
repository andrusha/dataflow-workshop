package me.andrusha.solutions

import akka.NotUsed
import akka.stream.scaladsl.Flow
import me.andrusha.WikipediaChanges.Change
import me.andrusha.WikipediaStats.DomainStats
import scala.concurrent.duration._

object WikipediaStats {
  def domainStats: Flow[Change, Set[DomainStats], NotUsed] = {
    // transform to common structure
    Flow[Change]
      .map(x => DomainStats(x.meta.domain, 1))
      // define window
      .groupedWithin(1e6.toInt, 10.seconds)
      // group by domain
      .map(_.groupBy(_.domain).values)
      // sum groups of the same domain together
      .map(_.map(_.reduceLeft((x, y) => DomainStats(x.domain, x.count + y.count))).toSet)
  }
}
