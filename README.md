![CI](https://github.com/rogervinas/spring-cloud-sleuth-demo/workflows/CI/badge.svg)

# Spring Cloud Sleuth Demo

Demo testing trace propagation across this flow:

```
   RestTemplate           
        |
  RestController /request1
        |
   KafkaProducer
        |
   ------------
     my.topic
   ------------
        |
   KafkaConsumer
        | 
   RestTemplate
        |
  RestController /request2
        | 
   FeignClient
        |
  RestController /request3
        | 
    WebClient
        |
  RestController /request4
        |             
      Async
        |
       End
```

## Documentation

* [Spring Cloud Sleuth Reference Documentation](https://docs.spring.io/spring-cloud-sleuth/docs/current-SNAPSHOT/reference/html/index.html)
* [Spring Cloud Sleuth customization](https://docs.spring.io/spring-cloud-sleuth/docs/current-SNAPSHOT/reference/html/integrations.html#sleuth-integration)