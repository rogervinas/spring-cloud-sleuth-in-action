package com.rogervinas.sleuth

import com.rogervinas.sleuth.helper.DockerComposeContainerHelper
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility.await
import org.awaitility.Durations.ONE_SECOND
import org.awaitility.Durations.TEN_SECONDS
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT
import org.springframework.boot.test.system.CapturedOutput
import org.springframework.boot.test.system.OutputCaptureExtension
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.function.Consumer
import java.util.regex.Pattern
import java.util.stream.Collectors

@SpringBootTest(webEnvironment = DEFINED_PORT)
@Testcontainers
@ExtendWith(OutputCaptureExtension::class)
class MyApplicationIntegrationTest {
  companion object {
    @Container
    val container = DockerComposeContainerHelper().createContainer()
  }

  @LocalServerPort
  var port: Int = 0

  val rest = RestTemplate()

  @Test
  fun `should propagate tracing`(log: CapturedOutput) {
    val traceId = "edb77ece416b3196"
    val spanId = "c58ac2aa66d238b9"

    val response = request1(traceId, spanId)

    assertThat(response.statusCode).isEqualTo(OK)
    assertThat(response.body).isEqualTo("ok")

    val logLines =
      await()
        .atMost(TEN_SECONDS)
        .pollDelay(ONE_SECOND)
        .until({ parseLogLines(log) }, { it.size >= 7 })

    assertThatLogLineContainsMessageAndTraceId(logLines[0], "RestRequest1 hello", traceId)
    assertThatLogLineContainsMessageAndTraceId(logLines[1], "KafkaProducer hello", traceId)
    assertThatLogLineContainsMessageAndTraceId(logLines[2], "KafkaConsumer hello", traceId)
    assertThatLogLineContainsMessageAndTraceId(logLines[3], "RestRequest2 hello", traceId)
    assertThatLogLineContainsMessageAndTraceId(logLines[4], "RestRequest3 hello", traceId)
    assertThatLogLineContainsMessageAndTraceId(logLines[5], "RestRequest4 hello", traceId)
    assertThatLogLineContainsMessageAndTraceId(logLines[6], "AsyncService hello", traceId)
  }

  private fun request1(
    traceId: String,
    spanId: String,
  ): ResponseEntity<String> {
    val requestUrl = "http://localhost:$port/request1?payload=hello"
    val requestHeaders = HttpHeaders()
    requestHeaders["X-B3-TraceId"] = traceId
    requestHeaders["X-B3-SpanId"] = spanId
    val request = HttpEntity<Unit>(requestHeaders)
    return rest.exchange(requestUrl, HttpMethod.GET, request, String::class.java)
  }

  private fun assertThatLogLineContainsMessageAndTraceId(
    logLine: LogLine,
    msg: String,
    traceId: String,
  ) {
    assertThat(logLine).satisfies(
      Consumer {
        assertThat(it.msg).isEqualTo(msg)
        assertThat(it.traceId).isEqualTo(traceId)
        assertThat(it.spanId).isNotBlank
      },
    )
  }

  private fun parseLogLines(log: CapturedOutput): List<LogLine> {
    return log.all.split(System.lineSeparator()).stream()
      .map { Pattern.compile(">>> (.+) - traceId (.+) spanId (.+) - .+").matcher(it) }
      .filter { it.matches() }
      .map { LogLine(it.group(1), it.group(2), it.group(3)) }
      .collect(Collectors.toList())
  }
}

class LogLine(val msg: String, val traceId: String, val spanId: String)
