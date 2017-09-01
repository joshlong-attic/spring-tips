package com.example

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.{ApplicationArguments, ApplicationRunner, SpringApplication}
import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.web.reactive.function.server.RequestPredicates._
import org.springframework.web.reactive.function.server.RouterFunctions._
import org.springframework.web.reactive.function.server._
import reactor.core.publisher.Flux

import scala.beans.BeanProperty
import scala.language.implicitConversions

/**
  * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
  */
@SpringBootApplication
class Application {

  @Bean
  def init(repository: CustomerRepository) = new ApplicationRunner() {

    override def run(applicationArguments: ApplicationArguments): Unit = {

      val value: Flux[Customer] =
        repository
          .deleteAll()
          .thenMany(Flux.just("Jane", "Dave", "Viktor", "Juergen"))
          .map({ n => Customer(null, n) })

      repository.saveAll(value).subscribe({ it => println(it) })
    }
  }
}

object Application extends App {
  SpringApplication.run(classOf[Application], args: _*)
}

@Document
case class Customer(@Id @BeanProperty var id: String, @BeanProperty var name: String)

trait CustomerRepository extends ReactiveMongoRepository[Customer, java.lang.String]

@Configuration
class FunctionalReactiveConfiguration(val customerRepository: CustomerRepository) {

  @Bean
  def routes(): RouterFunction[_] =
    route(GET("/customers"),
        (_) => ServerResponse.ok().body(customerRepository.findAll(), classOf[Customer]))
    .andRoute(GET("/customers/{id}"),
        (request) => ServerResponse.ok().body(customerRepository.findById(request.pathVariable("id")), classOf[Customer]))
}

/*

@RestController
class SimpleRestController(val customerRepository: CustomerRepository) {

  @GetMapping(Array("/customers"))
  def customers = customerRepository.findAll()
}*/
