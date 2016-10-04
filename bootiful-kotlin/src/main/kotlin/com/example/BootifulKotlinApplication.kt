package com.example

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import java.util.stream.Stream

@SpringBootApplication
open class BootifulKotlinApplication(val personRepository: PersonRepository) :
        CommandLineRunner {

    override fun run(vararg args: String?) {

        personRepository.deleteAll()

        Stream.of("Phil,Webb", "Dave,Syer", "Spencer,Gibb",
                "Brian,Clozel" , "Sebastien,Deleuze", "Mark,Fisher")
                .map { fn -> fn.split(",") }
                .forEach { tpl -> personRepository.save(Person(tpl[0], tpl[1])) }

        personRepository.all().forEach { println(it) }
    }
}

fun String.bootify () {
    "Bootiful " + this
}

fun main(args: Array<String>) {
    SpringApplication.run(BootifulKotlinApplication::class.java, *args)
}

interface PersonRepository : MongoRepository<Person, String> {

    @Query("{}")
    fun all(): Stream<Person>
}

@Document
data class Person(var first: String? = null,
                  var last: String? = null,
                  @Id var id: String? = null)