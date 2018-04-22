package jmailen.slakoverflow.slack

import io.ktor.client.request.post
import io.ktor.client.response.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import jmailen.http.Http
import jmailen.serialization.Json
import kotlinx.coroutines.experimental.runBlocking
import org.slf4j.LoggerFactory

class SlackClient {
    val logger = LoggerFactory.getLogger("SlackClient")

    fun respond(message: CommandResponse, toUrl: String) {
        val response = runBlocking {
            Http.client.post<HttpResponse>(toUrl) {
                contentType(ContentType.Application.Json)
                body = Json.write(message)
            }
        }

        when (response.status.value) {
            in 200..299 -> logger.info("sent response to: $toUrl")
            in 300..599 -> logger.error("response to: $toUrl failed with ${response.status}")
            else -> logger.warn("response to: $toUrl unexpected ${response.status}")
        }
    }
}
