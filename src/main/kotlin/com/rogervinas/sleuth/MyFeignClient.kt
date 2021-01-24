package com.rogervinas.sleuth

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "request3", url = "http://localhost:\${server.port}")
interface MyFeignClient {
    @RequestMapping(method = [RequestMethod.GET], path = ["/request3"])
    fun request3(@RequestParam("payload") payload: String)
}