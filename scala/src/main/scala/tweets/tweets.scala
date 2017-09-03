
package tweets

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import org.reactivestreams.Publisher
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.{ApplicationArguments, ApplicationRunner, SpringApplication}
import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.{GetMapping, RequestMapping, RestController}
import reactor.core.publisher.Flux

import scala.beans.BeanProperty
import scala.collection.JavaConverters

/**
  * @author <a href="josh@joshlong.com">Josh Long</a>
  */
@SpringBootApplication
class Application

object Application extends App {
  SpringApplication.run(classOf[Application], args: _*)
}


@Configuration
class AkkaConfiguration {

  @Bean
  def actorSystem(): ActorSystem = ActorSystem.create("bootifulScala")

  @Bean
  def materializer(actorSystem: ActorSystem): ActorMaterializer = ActorMaterializer.create(actorSystem)
}

@RestController
@RequestMapping(path = Array("/tweets"), produces = Array(MediaType.APPLICATION_JSON_UTF8_VALUE))
class TweetRestController(tweetRepository: TweetRepository,
                          materializer: ActorMaterializer) {

  @GetMapping
  def tweets: Publisher[Tweet] = tweetRepository.findAll()

  @GetMapping(Array("/unique-hashtags"))
  def uniqueHashTags(): Publisher[HashTag] = {

    val src: Source[Tweet, NotUsed] = Source.fromPublisher(tweetRepository.findAll())
    val akkaStreamsPublisher =
      src
        .map(a => JavaConverters.asScalaSet(a.hashtags).toSet) // Get all sets of hashtags ...
        .reduce((a, b) => a ++ b) // ... and reduce them to a single set, removing duplicates across all tweets
        .mapConcat(identity) // Flatten the stream of tweets to a stream of hashtags
        .map(x => x.name.toLowerCase()) // Convert all hashtags to upper case
        .map(x => HashTag(x))
        .runWith(Sink.asPublisher(true)) {
          materializer
        }
    Flux.from(akkaStreamsPublisher)
  }
}

@Component
class TweetInitializer(tweetRepository: TweetRepository) extends ApplicationRunner {

  override def run(applicationArguments: ApplicationArguments): Unit = {

    val a = Author("usera")
    val b = Author("userb")
    val c = Author("userc")

    val tweets: Flux[Tweet] = Flux.just(
      Tweet(a, "i <3 #akka"),
      Tweet(b, "I <3 #spring"),
      Tweet(c, "I <3 #spring and #akka"),
      Tweet(b, "I dig #scala"),
      Tweet(a, "I dig #boot"),
      Tweet(a, "I use #boot and #scala together"))
      .flatMap(tweetRepository.save(_))

    tweetRepository
      .deleteAll()
      .thenMany(tweets)
      .thenMany(tweetRepository.findAll())
      .subscribe((it: Tweet) => println(
        s"""
            $it has the following hashtags: ${it.hashtags}
          """.trim)
      )
  }
}

trait TweetRepository extends ReactiveMongoRepository[Tweet, String]

@Document
case class Author(@Id @BeanProperty handle: String)

@Document
case class HashTag(@Id @BeanProperty name: String)

@Document
case class Tweet(@BeanProperty author: Author, @BeanProperty body: String) {

  @BeanProperty
  val hashtags: java.util.Set[HashTag] = JavaConverters.setAsJavaSet(
    body
      .split(" ")
      .collect {
        case t if t.startsWith("#") => HashTag(t.replaceAll("[^#\\w]", ""))
      }
      .toSet
  )
}