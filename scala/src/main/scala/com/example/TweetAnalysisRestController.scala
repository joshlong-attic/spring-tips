package com.example

import java.util

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.{GetMapping, PathVariable, RestController}
import reactor.core.publisher.Flux

import scala.collection.JavaConverters

/**
  * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
  */

@Configuration
class AkkaConfiguration {

  @Bean
  def actorSystem(): ActorSystem = ActorSystem.create("bootifulScala")

  @Bean
  def materializer(actorSystem: ActorSystem): ActorMaterializer = ActorMaterializer.create(actorSystem)
}

@RestController
class TweetAnalysisRestController(val tweetAnalysisService: TweetAnalysisService) {

  @GetMapping(Array("/tweets/{hash}"))
  def analyse(@PathVariable hash: String): Flux[String] = tweetAnalysisService.analyseTweets(hash)
}

@Service
class TweetAnalysisService(system: ActorSystem, m: ActorMaterializer) {

  def analyseTweets(hash: String = "#akka"): Flux[String] = {
//    val h = if (hash.startsWith("#")) hash else "#" + hash
//    val akkaTag = HashTag(h)

    val juListOfTweets: util.List[Tweet] = util.Arrays.asList(Tweet(Author("rolandkuhn"), System.currentTimeMillis, "#akka rocks!")
      , Tweet(Author("patriknw"), System.currentTimeMillis, "#akka !")
      , Tweet(Author("bantonsson"), System.currentTimeMillis, "#akka !")
      , Tweet(Author("drewhk"), System.currentTimeMillis, "#akka !")
      , Tweet(Author("ktosopl"), System.currentTimeMillis, "#akka on the rocks!")
      , Tweet(Author("mmartynas"), System.currentTimeMillis, "wow #akka !")
      , Tweet(Author("akkateam"), System.currentTimeMillis, "#akka rocks!")
      , Tweet(Author("bananaman"), System.currentTimeMillis, "#bananas rock!")
      , Tweet(Author("appleman"), System.currentTimeMillis, "#apples rock!")
      , Tweet(Author("drama"), System.currentTimeMillis, "we compared #apples to #oranges!")
    )
    val listOfTweets: List[Tweet] = JavaConverters.asScalaBuffer(juListOfTweets).toList
    val tweets: Source[Tweet, NotUsed] = Source(listOfTweets) //Source(listOfTweets)

    Flux.from(
      tweets
        .map(a => a.hashtags) // Get all sets of hashtags ...
        .reduce((a, b) => a ++ b) // ... and reduce them to a single set, removing duplicates across all tweets
        .mapConcat(identity) // Flatten the stream of tweets to a stream of hashtags
        .map(x => x.name.toUpperCase) // Convert all hashtags to upper case
        .runWith(Sink.asPublisher(true)) {
        m
      }) // convert it to a publisher
  }
}

case class Author(handle: String)

case class HashTag(name: String)

case class Tweet(author: Author, timestamp: Long, body: String) {

  def hashtags: Set[HashTag] =
    body
      .split(" ")
      .collect {
        case t if t.startsWith("#") => HashTag(t.replaceAll("[^#\\w]", ""))
      }
      .toSet
}
