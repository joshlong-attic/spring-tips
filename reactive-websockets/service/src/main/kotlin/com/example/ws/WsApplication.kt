package com.example.ws

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.channel.PublishSubscribeChannel
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.dsl.IntegrationFlows
import org.springframework.integration.file.dsl.Files
import org.springframework.messaging.Message
import org.springframework.messaging.MessageHandler
import org.springframework.web.reactive.HandlerMapping
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink
import reactor.core.publisher.SynchronousSink
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Consumer

@SpringBootApplication
class WsApplication

fun main(args: Array<String>) {
    SpringApplication.run(WsApplication::class.java, *args)
}


data class FileEvent(val sessionId: String, val path: String)


@Configuration
class WebSocketConfiguration {

    @Bean
    fun fileChannel() = PublishSubscribeChannel()

    @Bean
    fun filesFlow(@Value("\${file://\${HOME}/Desktop/in}") file: File): IntegrationFlow =
            IntegrationFlows
                    .from(Files
                            .inboundAdapter(file)
                            .autoCreateDirectory(true),
                            { p -> p.poller({ pm -> pm.fixedRate(1000) }) })
                    .channel(fileChannel())
                    .get()


    @Bean
    fun wsha() = WebSocketHandlerAdapter()

    @Bean
    fun wsh(): WebSocketHandler {
        val channel = fileChannel()
        val connections = ConcurrentHashMap<String, MessageHandler>()
        val om = ObjectMapper()

        class ForwardingMessageHandler(val sink: FluxSink<WebSocketMessage>,
                                       val session: WebSocketSession) : MessageHandler {

            private val sessionId = session.id

            override fun handleMessage(p0: Message<*>) {
                val file = p0.payload as File
                val fe = FileEvent(sessionId = sessionId, path = file.absolutePath)
                val json = om.writeValueAsString(fe)
                val textMessage = session.textMessage(json)
                sink.next(textMessage)
            }
        }

        return WebSocketHandler { session ->
            val sessionId = session.id
            val delayedMessages = Flux.create (
                    Consumer<FluxSink<WebSocketMessage>> { sink ->
                        connections.put(sessionId, ForwardingMessageHandler(sink, session))
                        channel.subscribe(connections.get(sessionId))
                    })
                    .doFinally {
                        channel.unsubscribe(connections.get(sessionId))
                        connections.remove(sessionId)
                    }
            return@WebSocketHandler session.send(delayedMessages)
        }
    }

    @Bean
    fun hm(): HandlerMapping {
        val hm = SimpleUrlHandlerMapping()
        hm.urlMap = mapOf("/ws/files" to wsh())
        hm.order = 10
        return hm
    }
}