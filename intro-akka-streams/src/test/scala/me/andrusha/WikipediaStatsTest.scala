package me.andrusha

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import org.scalatest.FunSpec

import scala.concurrent.Await
import scala.concurrent.duration._

class WikipediaStatsTest extends FunSpec {
  implicit val system: ActorSystem = ActorSystem("WikipediaChanesTest")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  describe("domainStats") {
    it("should domainStats") {
      val source = Source(List(
        WikipediaChanges.Change(true, "", WikipediaChanges.Meta("", "")),
        WikipediaChanges.Change(false, "", WikipediaChanges.Meta("", "")),
        WikipediaChanges.Change(true, "", WikipediaChanges.Meta("", "")),
        WikipediaChanges.Change(true, "", WikipediaChanges.Meta("", "")),
      ))
      val future = source.via(WikipediaStats.botEdits).runFold(List.empty[Int])(_ :+ _)
      val result = Await.result(future, 5.seconds)

      assert(result == List(3))
    }
  }

  describe("botEdits") {
    it("count edits made by bots") {
      val source = Source(List(
        WikipediaChanges.Change(true, "", WikipediaChanges.Meta("", "ru.domain")),
        WikipediaChanges.Change(false, "", WikipediaChanges.Meta("", "ru.domain")),
        WikipediaChanges.Change(true, "", WikipediaChanges.Meta("", "en.domain")),
        WikipediaChanges.Change(true, "", WikipediaChanges.Meta("", "eu.domain")),
      ))
      val future = source.via(WikipediaStats.domainStats).runFold(List.empty[Set[WikipediaStats.DomainStats]])(_ :+ _)
      val result = Await.result(future, 5.seconds)

      assert(result == List(Set(
        WikipediaStats.DomainStats("ru.domain", 2),
        WikipediaStats.DomainStats("en.domain", 1),
        WikipediaStats.DomainStats("eu.domain", 1)
      )))
    }
  }
}
