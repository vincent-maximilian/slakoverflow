package jmailen.slakoverflow.stackoverflow

import io.ktor.client.request.get
import io.ktor.client.response.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import jmailen.http.Http
import jmailen.java.urlEncode
import jmailen.serialization.Json
import kotlinx.coroutines.experimental.runBlocking
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.net.URI
import java.util.zip.GZIPInputStream
import kotlin.collections.set

class StackOverflowClient(val stackAppKey: String? = null) {
    companion object {
        val logger = LoggerFactory.getLogger(StackOverflowClient::class.java)
    }

    fun siteInfo(): List<SiteInfo> {
        val call = ApiCall("/info", stackAppKey = stackAppKey)
        logger.info("siteInfo: ${call.url()}")

        return call.get().readJson<SiteInfos>().items
    }

    fun excerptSearch(freeText: String): List<SearchResultExcerpt> {
        val call = ApiCall("/search/excerpts", stackAppKey = stackAppKey).apply {
            params["order"] = "desc"
            params["sort"] = "relevance"
            params["q"] = freeText
        }
        logger.info("excerptSearch: ${call.url()}")

        return call.get().readJson<SearchResults>().items
    }

    fun answers(questionId: Int): List<Answer> {
        val call = ApiCall("/questions/$questionId/answers", stackAppKey = stackAppKey).apply {
            params["order"] = "desc"
            params["sort"] = "votes"
        }
        logger.info("answers: ${call.url()}")

        return call.get().readJson<Answers>().items
    }

    fun pageFor(questionId: Int) = QuestionPage(questionId)
}

class ApiCall(val path: String = "", site: String = ApiCall.STACKOVERFLOW_SITE, stackAppKey: String? = null) {

    companion object {
        const val API_ROOT = "https://api.stackexchange.com/2.2"
        const val STACKOVERFLOW_SITE = "stackoverflow"
    }

    var params = LinkedHashMap<String, String>()

    init {
        params.set("site", site)
        stackAppKey?.let { key -> params.set("key", key) }
    }

    fun get(): InputStream {
        val response = runBlocking {
            Http.client.get<HttpResponse>(url()) {
                contentType(ContentType.Application.Json)
            }
        }
        return when (response.headers[HttpHeaders.ContentEncoding]) {
            "gzip" -> GZIPInputStream(response.receiveContent().inputStream())
            else -> response.receiveContent().inputStream()
        }
    }

    fun url() = URI(API_ROOT + path + paramsString()).toURL()

    private fun paramsString() =
            when (params.isEmpty()) {
                true -> ""
                false -> "?" + params.map {
                    "${it.key.urlEncode()}=${it.value.urlEncode()}"
                }.joinToString("&")
            }
}

inline fun <reified T : Any> InputStream.readJson(): T = Json.read(readAllBytes())
