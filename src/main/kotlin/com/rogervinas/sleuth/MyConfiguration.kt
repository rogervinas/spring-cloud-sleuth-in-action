package com.rogervinas.sleuth

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class MyConfiguration {

    @Bean
    fun restTemplate() = RestTemplate()

    @Bean
    fun webClient() = WebClient.create()
}