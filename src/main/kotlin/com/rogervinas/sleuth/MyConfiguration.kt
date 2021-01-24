package com.rogervinas.sleuth

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class MyConfiguration {

    @Bean
    fun restTemplate() = RestTemplate()
}