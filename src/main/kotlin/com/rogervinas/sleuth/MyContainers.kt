package com.rogervinas.sleuth

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.containers.wait.strategy.Wait.forListeningPort
import org.testcontainers.containers.wait.strategy.Wait.forLogMessage
import org.testcontainers.containers.wait.strategy.WaitAllStrategy
import java.io.File
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Component
@Profile("docker-compose")
class MyContainers {

    private val KAFKA = "kafka"
    private val KAFKA_PORT = 9094

    private val ZOOKEEPER = "zookeeper"
    private val ZOOKEEPER_PORT = 2181

    private val ZIPKIN = "zipkin"
    private val ZIPKIN_PORT = 9411

    private val container = DockerComposeContainer<Nothing>(File("docker-compose.yml"))
            .apply { withLocalCompose(true) }
            .apply { withExposedService(KAFKA, KAFKA_PORT) }
            .apply {
                waitingFor(KAFKA, WaitAllStrategy(WaitAllStrategy.Mode.WITH_INDIVIDUAL_TIMEOUTS_ONLY)
                        .apply { withStrategy(forListeningPort()) }
                        .apply { withStrategy(forLogMessage(".*creating topics.*", 1)) }
                )
            }
            .apply { withExposedService(ZOOKEEPER, ZOOKEEPER_PORT) }
            .apply {
                waitingFor(ZOOKEEPER, WaitAllStrategy(WaitAllStrategy.Mode.WITH_INDIVIDUAL_TIMEOUTS_ONLY)
                        .apply { withStrategy(forListeningPort()) }
                        .apply { withStrategy(forLogMessage(".*binding to port.*", 1)) }
                )
            }
            .apply { withExposedService(ZIPKIN, ZIPKIN_PORT) }

    @PostConstruct
    fun start() = container.start()

    @PreDestroy
    fun stop() = container.stop()
}