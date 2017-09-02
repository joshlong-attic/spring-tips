package com.example.akka

import akka.actor.{Actor, ActorSystem, Extension, IndirectActorProducer, Props}
import com.typesafe.config.{Config, ConfigFactory}
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.{Bean, Configuration}

/**
  * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
  */
@Configuration
class AkkaConfiguration {

  @Bean
  def actorSystem(config: Config): ActorSystem = ActorSystem.create("akkaSystemName", config)

  @Bean
  def akkaConfiguration: Config = ConfigFactory.load

  @Bean
  def springExtension(ac: ApplicationContext) = new SpringExtension(ac)
}

class SpringActorProducer(val actorBeanName: String,
                          val ac: ApplicationContext) extends IndirectActorProducer {

  override def produce(): Actor = ac.getBean(actorBeanName, classOf[Actor])

  override def actorClass: Class[Actor] = classOf[Actor]
}

class SpringExtension(val context: ApplicationContext) extends Extension {

  def springPropertiesForActor(actorBeanName: String): Props =
    Props.create(classOf[SpringActorProducer], this.context, actorBeanName)
}