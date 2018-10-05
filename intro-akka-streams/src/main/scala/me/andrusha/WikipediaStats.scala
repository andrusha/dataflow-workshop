package me.andrusha

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, RunnableGraph, Sink}
import akka.stream.{ActorMaterializer, ClosedShape}

import scala.concurrent.duration._

// Don't do let this pass on review
import me.andrusha.WikipediaChanges.{Change, httpResponseToLines, parseJson, wikiDataFilter, wikipediaSource}


object WikipediaStats {

  implicit val system: ActorSystem = ActorSystem("WikipediaStats")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  case class DomainStats(domain: String, count: Int)

  /**
    * Let's make a couple of parallel computations on the same stream,
    * we'll produce a number of metrics
    *
    * @param args
    */
  def main(args: Array[String]): Unit = {
    RunnableGraph.fromGraph(GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>
      import GraphDSL.Implicits._

      val wiki = wikipediaSource
        .via(httpResponseToLines)
        .via(wikiDataFilter)
        .via(parseJson)

      val bcast = builder.add(Broadcast[Change](2))

      wiki ~> bcast ~> botEdits ~> Sink.foreach[Int](println(_))
              bcast ~> Sink.ignore

      ClosedShape
    }).run()
  }

  /**
    * Counts number of bot edits per time period
    * @return
    */
  def botEdits: Flow[Change, Int, NotUsed] = {
    Flow[Change]
      .filter(_.bot)
      .groupedWithin(1e6.toInt, 10.seconds)
      .map(_.length)
  }

  /**
    * @todo count human edits per wikipedia domain (eg russian, english, etc)
    *
    * @return
    */
  def domainStats: Flow[Change, Set[DomainStats], NotUsed] = ???
}
