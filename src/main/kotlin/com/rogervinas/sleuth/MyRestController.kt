package com.rogervinas.sleuth

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class MyRestController(
        private val producer: MyKafkaProducer,
        private val feign: MyFeignClient
) {

    val logger = LoggerFactory.getLogger(MyRestController::class.java)

    @GetMapping("/request1")
    fun request1(@RequestParam("payload") payload: String) : String {
        logger.info(">>> Request1 $payload")
        producer.produce(payload)
        return "ok"
    }

    @GetMapping("/request2")
    fun request2(@RequestParam("payload") payload: String) : String {
        logger.info(">>> Request2 $payload")
        feign.request3(payload)
        return "ok"
    }

    @GetMapping("/request3")
    fun request3(@RequestParam("payload") payload: String) : String {
        logger.info(">>> Request3 $payload")
        return "ok"
    }
}