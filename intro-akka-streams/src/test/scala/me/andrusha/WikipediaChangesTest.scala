package me.andrusha

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpEntity, HttpResponse}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import org.scalatest.FunSpec

import scala.concurrent.Await
import scala.concurrent.duration._

class WikipediaChangesTest extends FunSpec {
  implicit val system: ActorSystem = ActorSystem("WikipediaChanesTest")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  describe("wikiDataFilter") {
    it("keeps values which start with `data:` drops the prefix") {
      val source = Source(List("drop: 1", "data: content", "id: 123"))
      val future = source.via(WikipediaChanges.wikiDataFilter).runFold(List.empty[String])(_ :+ _)
      val result = Await.result(future, 5.seconds)

      assert(result == List("content"))
    }
  }

  describe("httpResponseToLines") {
    it("splits http response into flat stream of lines") {
      val source = Source(List(HttpResponse(entity = HttpEntity("line1\nline2\nline3\n\n"))))
      val future = source.via(WikipediaChanges.httpResponseToLines).runFold(List.empty[String])(_ :+ _)
      val result = Await.result(future, 5.seconds)

      assert(result == List("line1", "line2", "line3", ""))
    }
  }

  describe("parseJson") {
    it("ignores malformed JSON, missing fields, wrong payload") {
      val source = Source(List(" ", """{"x":1,,  """, """{"bot":true,"comment":0}"""))
      val future = source.via(WikipediaChanges.parseJson).runFold(List.empty[WikipediaChanges.Change])(_ :+ _)
      val result = Await.result(future, 5.seconds)

      assert(result == Nil)
    }

    it("parses correct payload") {
      val source = Source(List(
        """
          |{"bot": true, "comment": "hello", "meta": {"uri": "http://something.com", "domain": "something.com"}}
        """.stripMargin))
      val future = source.via(WikipediaChanges.parseJson).runFold(List.empty[WikipediaChanges.Change])(_ :+ _)
      val result = Await.result(future, 5.seconds)
      val parsed = WikipediaChanges.Change(
        bot = true,
        comment = "hello",
        meta = WikipediaChanges.Meta(
          uri = "http://something.com",
          domain = "something.com"))

      assert(result == List(parsed))
    }
  }

  describe("userCommentExtractor") {
    it("keeps only user comments") {
      val source = Source(List(
        WikipediaChanges.Change(true, "bot comment", WikipediaChanges.Meta("", "")),
        WikipediaChanges.Change(false, "user comment", WikipediaChanges.Meta("", "")),
        WikipediaChanges.Change(true, "another bot comment", WikipediaChanges.Meta("", "")),
        WikipediaChanges.Change(false, "another user comment", WikipediaChanges.Meta("", "")),
      ))
      val future = source.via(WikipediaChanges.userCommentExtractor).runFold(List.empty[String])(_ :+ _)
      val result = Await.result(future, 5.seconds)

      assert(result == List("user comment", "another user comment"))
    }
  }
}
