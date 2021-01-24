package com.rogervinas.sleuth

import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility.await
import org.awaitility.Durations.ONE_SECOND
import org.awaitility.Durations.TEN_SECONDS
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.system.CapturedOutput
import org.springframework.boot.test.system.OutputCaptureExtension
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus.OK
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.client.RestTemplate
import java.util.regex.Pattern
import java.util.stream.Collectors

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ExtendWith(SpringExtension::class, OutputCaptureExtension::class)
class ApplicationShould {

    @LocalServerPort
    var port: Int = 0

    val rest = RestTemplate()

    @Test
    fun `propagate tracing`(log: CapturedOutput) {
        val response = rest.getForEntity("http://localhost:$port/request1?payload=hello", String::class.java)

        assertThat(response.statusCode).isEqualTo(OK)
        assertThat(response.body).isEqualTo("ok")

        val logLines = await()
                .atMost(TEN_SECONDS)
                .pollDelay(ONE_SECOND)
                .until({ parseLogLines(log) }, { logLines -> logLines.size == 3 })

        assertThat(logLines[0]).satisfies {
            assertThat(it.msg).isEqualTo("Request1 hello")
            assertThat(it.traceId).isNotBlank
            assertThat(it.spanId).isNotBlank
        }
        assertThat(logLines[1]).satisfies {
            assertThat(it.msg).isEqualTo("KafkaProducer hello")
            assertThat(it.traceId).isEqualTo(logLines[0].traceId)
            assertThat(it.spanId).isNotBlank
        }
        assertThat(logLines[2]).satisfies {
            assertThat(it.msg).isEqualTo("KafkaConsumer hello")
            assertThat(it.traceId).isEqualTo(logLines[0].traceId)
            assertThat(it.spanId).isNotBlank
        }
    }

    fun parseLogLines(log: CapturedOutput): List<LogLine> {
        return log.all.split(System.lineSeparator()).stream()
                .map { Pattern.compile(">>> (.+) - traceId (.+) spanId (.+)").matcher(it) }
                .filter { it.matches() }
                .map { LogLine(it.group(1), it.group(2), it.group(3)) }
                .collect(Collectors.toList())
    }
}

class LogLine(val msg: String, val traceId: String, val spanId: String)
