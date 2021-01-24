package com.rogervinas.sleuth

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients
class MyApplication

fun main(args: Array<String>) {
	runApplication<MyApplication>(*args)
}
