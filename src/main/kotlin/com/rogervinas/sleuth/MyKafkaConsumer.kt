package com.rogervinas.sleuth

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.messaging.Message
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component("consumer")
class MyKafkaConsumer(
  @Value("\${server.port}") private val port: Int,
  private val rest: RestTemplate,
) : (Message<String>) -> Unit {
  companion object {
    private val LOGGER = LoggerFactory.getLogger(MyKafkaConsumer::class.java)
  }

  override fun invoke(message: Message<String>) {
    LOGGER.info(">>> KafkaConsumer ${message.payload}")
    rest.getForEntity("http://localhost:$port/request2?payload=${message.payload}", String::class.java)
  }
}
