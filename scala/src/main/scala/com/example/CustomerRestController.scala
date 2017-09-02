package com.example

import org.springframework.boot.ApplicationArguments
import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.web.bind.annotation.{GetMapping, RestController}
import reactor.core.publisher.Flux

import scala.beans.BeanProperty

/**
  * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
  */
@RestController
class CustomerRestController(val customerRepository: CustomerRepository) {

  @GetMapping(Array("/customers"))
  def customers: Flux[Customer] = customerRepository.findAll()
}

@Document
case class Customer(@Id @BeanProperty var id: String, @BeanProperty var name: String)

trait CustomerRepository extends ReactiveMongoRepository[Customer, java.lang.String]

@Configuration
class CustomerConfiguration {

  @Bean
  def init(repository: CustomerRepository) = (args: ApplicationArguments) => {
    val value: Flux[Customer] =
      repository
        .deleteAll()
        .thenMany(Flux.just("Juergen", "Jane", "Mhadura", "Dave", "Viktor", "Roland"))
        .map((n: String) => Customer(null, n))

    repository.saveAll(value).subscribe({ it => println(it) })
  }
}

/*
@Configuration
class FunctionalReactiveConfiguration(val customerRepository: CustomerRepository) {

  @Bean
  def routes(): RouterFunction[_] =
    route(GET("/customers"), (_) => ServerResponse.ok().body(customerRepository.findAll(), classOf[Customer]))
      .andRoute(GET("/customers/{id}"), (request) => ServerResponse.ok().body(customerRepository.findById(request.pathVariable("id")), classOf[Customer]))
}*/
