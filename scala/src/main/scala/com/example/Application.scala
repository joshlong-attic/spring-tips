package com.example

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.{ApplicationArguments, SpringApplication}
import org.springframework.context.annotation.Bean
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.web.bind.annotation.{GetMapping, PathVariable, RestController}
import reactor.core.publisher.Flux

import scala.beans.BeanProperty
import scala.language.implicitConversions

/**
  * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
  */
@SpringBootApplication
class Application {

}

object Application extends App {
  SpringApplication.run(classOf[Application], args: _*)
}

