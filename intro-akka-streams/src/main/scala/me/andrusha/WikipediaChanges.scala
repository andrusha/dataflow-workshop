package me.andrusha

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Source}
import io.circe.generic.JsonCodec

object WikipediaChanges {
  implicit val system: ActorSystem = ActorSystem("WikipediaChanges")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  @JsonCodec
  case class Change(bot: Boolean, comment: String, meta: Meta)
  @JsonCodec
  case class Meta(uri: String, domain: String)

  /**
    * @todo To get familiar with akka-streams let's modify
    *   the code to have stream of comments made by real users
    *
    * @param args
    */
  def main(args: Array[String]): Unit = {
    wikipediaSource
      .via(httpResponseToLines)
      .via(wikiDataFilter)
      .via(parseJson)
      .via(userCommentExtractor)
      .runForeach(println(_))
  }

  /**
    * Stream of changes to wikipedia articles. Wikipedia employs long-polling
    * and just constantly appends new content to opened http connection.
    *
    * @return streamed http response
    */
  def wikipediaSource: Source[HttpResponse, NotUsed] = {
    Source
      .fromFuture(
        Http()
          .singleRequest(HttpRequest(uri = "https://stream.wikimedia.org/v2/stream/recentchange")))
  }

  /**
    * Every event in the stream comes in triples each occupying its own line, eg:
    *
    * event: message
    * id: [{"topic":"eqiad.mediawiki.recentchange","partition":0,"offset":-1}, ...]
    * data: {"bot":false,"comment":"upd","id":1092579436,"length":{"new":2264,"old":2264}, ...}
    *
    * @return
    */
  def wikiDataFilter: Flow[String, String, NotUsed] = {
    val dataPrefix = "data: "

    Flow[String]
      .filter(_.startsWith(dataPrefix))
      .map(_.drop(dataPrefix.length))
  }

  /**
    * Response might arrive chunked, so we have to concat and
    * split lines before processing
    *
    * @return http response lines
    */
  def httpResponseToLines: Flow[HttpResponse, String, NotUsed] = {
    Flow[HttpResponse]
      .flatMapConcat(_.entity.dataBytes)
      .map(_.utf8String)
      .mapConcat(_.lines.toList)
  }

  /**
    * Parses JSON ignoring errors
    *
    * Example payload:
    * {
    *   "bot": false,
    *   "comment": "Bibliotekoj",
    *   "id": 23502282,
    *   "length": {
    *     "new": 11626,
    *     "old": 11610
    *   },
    *   "meta": {
    *     "domain": "eo.wikipedia.org",
    *     "dt": "2018-09-29T18:35:37+00:00",
    *     "id": "76872977-c416-11e8-90d7-d094663b2c3f",
    *     "request_id": "aad5bead-8d2f-438b-928e-e70dca81f0c5",
    *     "schema_uri": "mediawiki/recentchange/2",
    *     "topic": "codfw.mediawiki.recentchange",
    *     "uri": "https://eo.wikipedia.org/wiki/Ordinara_hipokastano",
    *     "partition": 0,
    *     "offset": 64175008
    *   },
    *   "minor": true,
    *   "namespace": 0,
    *   "parsedcomment": "Bibliotekoj",
    *   "revision": {
    *     "new": 6430916,
    *     "old": 6206386
    *   },
    *   "server_name": "eo.wikipedia.org",
    *   "server_script_path": "/w",
    *   "server_url": "https://eo.wikipedia.org",
    *   "timestamp": 1538246137,
    *   "title": "Ordinara hipokastano",
    *   "type": "edit",
    *   "user": "Reclus",
    *   "wiki": "eowiki"
    * }
    *
    * @return parsed payload
    */
  def parseJson: Flow[String, Change, NotUsed] = {
    import io.circe.parser._

    Flow[String]
      .map(decode[Change](_))
      .filter(_.isRight)
      .map(_.right.get)
  }

  /**
    * @todo Extract comments from real users
    * @return
    */
  def userCommentExtractor: Flow[Change, String, NotUsed] = ???
}
