package com.rogervinas.sleuth

import org.slf4j.LoggerFactory
import org.springframework.messaging.Message
import org.springframework.stereotype.Component
import java.util.function.Consumer

@Component("consumer")
class MyKafkaConsumer : Consumer<Message<String>> {

    val logger = LoggerFactory.getLogger(MyKafkaConsumer::class.java)

    override fun accept(message: Message<String>) {
        logger.info(">>> KafkaConsumer ${message.payload}")
    }
}