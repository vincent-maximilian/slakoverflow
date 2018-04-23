package jmailen.http

import io.ktor.client.HttpClient
import io.ktor.client.call.call
import io.ktor.client.engine.apache.Apache
import io.ktor.client.response.HttpResponse
import io.ktor.content.TextContent
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import jmailen.serialization.Json
import jmailen.slakoverflow.Logger
import kotlinx.coroutines.experimental.runBlocking
import java.util.zip.GZIPInputStream

/**
 * Simplistic synchronous rest client atop Ktor http client.
 */
object RestClient {
    val http = HttpClient(Apache)

    inline fun <reified T : Any> get(url: String, message: Any? = null): T {
        return request(HttpMethod.Get, url, message)
    }

    inline fun <reified T : Any> post(url: String, message: Any? = null): T {
        return request(HttpMethod.Post, url, message)
    }

    inline fun <reified T : Any> request(httpMethod: HttpMethod, url: String, message: Any?) = runBlocking {
        val call = http.call(url) {
            method = httpMethod
            message?.let { body = TextContent(Json.write(message), ContentType.Application.Json) }
        }

        with (call.response) {
            when (status.value) {
                in 200..299 ->
                    Logger.debug("request successful to {}, received {} bytes", url, headers[HttpHeaders.ContentLength] ?: "0")
                else ->
                    throw RuntimeException("unexpected response to $url: state = $status, message = ${String(receiveBytes())}")
            }
            Json.read<T>(receiveBytes())
        }
    }
}

fun HttpResponse.receiveBytes() = when (headers[HttpHeaders.ContentEncoding]) {
    "gzip" -> GZIPInputStream(receiveContent().inputStream())
    else -> receiveContent().inputStream()
}.readAllBytes()
