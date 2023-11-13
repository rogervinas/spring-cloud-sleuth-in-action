package com.rogervinas.sleuth

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@RestController
class MyRestController(
    @Value("\${server.port}") private val port: Int,
    private val kafkaProducer: MyKafkaProducer,
    private val feignClient: MyFeignClient,
    private val webClient: WebClient,
    private val asyncService: MyAsyncService
) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(MyRestController::class.java)
    }

    @GetMapping("/request1")
    fun request1(@RequestParam("payload") payload: String): String {
        LOGGER.info(">>> RestRequest1 $payload")
        kafkaProducer.produce(payload)
        return "ok"
    }

    @GetMapping("/request2")
    fun request2(@RequestParam("payload") payload: String): String {
        LOGGER.info(">>> RestRequest2 $payload")
        return feignClient.request3(payload)
    }

    @GetMapping("/request3")
    fun request3(@RequestParam("payload") payload: String): Mono<String> {
        LOGGER.info(">>> RestRequest3 $payload")
        return webClient.get().uri("http://localhost:$port/request4?payload=$payload")
            .retrieve().bodyToMono(String::class.java);
    }

    @GetMapping("/request4")
    fun request4(@RequestParam("payload") payload: String): Mono<String> {
        LOGGER.info(">>> RestRequest4 $payload")
        return Mono.just(asyncService.execute(payload).get())
    }
}