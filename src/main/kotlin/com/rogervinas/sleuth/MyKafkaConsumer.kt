package com.rogervinas.sleuth

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.messaging.Message
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.util.function.Consumer

@Component("consumer")
class MyKafkaConsumer(
        @Value("\${server.port}") private val port: Int,
        private val rest: RestTemplate
) : Consumer<Message<String>> {

    val logger = LoggerFactory.getLogger(MyKafkaConsumer::class.java)

    override fun accept(message: Message<String>) {
        logger.info(">>> KafkaConsumer ${message.payload}")
        rest.getForEntity("http://localhost:$port/request2?payload=${message.payload}", String::class.java)
    }
}