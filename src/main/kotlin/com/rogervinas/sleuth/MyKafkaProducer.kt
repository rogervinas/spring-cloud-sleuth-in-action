package com.rogervinas.sleuth

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.BeanFactory
import org.springframework.cloud.sleuth.instrument.messaging.MessagingSleuthOperators
import org.springframework.messaging.Message
import org.springframework.messaging.support.GenericMessage
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import reactor.core.publisher.Sinks.EmitFailureHandler.FAIL_FAST

@Component("producer")
class MyKafkaProducer(private val beanFactory: BeanFactory) : () -> Flux<Message<String>> {
  companion object {
    private val LOGGER = LoggerFactory.getLogger(MyKafkaProducer::class.java)
  }

  private val sink = Sinks.many().unicast().onBackpressureBuffer<Message<String>>()

  fun produce(payload: String) {
    LOGGER.info(">>> KafkaProducer $payload")
    sink.emitNext(createMessageWithTracing(payload), FAIL_FAST)
  }

  private fun createMessageWithTracing(payload: String): Message<String> {
    return MessagingSleuthOperators.handleOutputMessage(
      beanFactory,
      MessagingSleuthOperators.forInputMessage(beanFactory, GenericMessage(payload)),
    )
  }

  override fun invoke() = sink.asFlux()
}
