package me.andrusha.solutions

import akka.NotUsed
import akka.stream.scaladsl.Flow
import me.andrusha.WikipediaChanges.Change

object WikipediaChanges {
  def userCommentExtractor: Flow[Change, String, NotUsed] = {
    // filter out bots
    Flow[Change]
      .filter(_.bot == false)
      // extract comments
      .map(_.comment)
  }
}
