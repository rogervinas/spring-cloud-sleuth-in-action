package com.rogervinas.sleuth

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.util.concurrent.CompletableFuture

@Service
class MyAsyncService {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(MyAsyncService::class.java)
    }

    @Async
    fun execute(payload: String): CompletableFuture<String> {
        LOGGER.info(">>> AsyncService $payload")
        return CompletableFuture.completedFuture("ok")
    }
}