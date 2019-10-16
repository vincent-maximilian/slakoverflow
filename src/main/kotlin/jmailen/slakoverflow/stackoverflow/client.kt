package jmailen.slakoverflow.stackoverflow

import java.net.URI
import jmailen.http.RestClient
import jmailen.java.urlEncode
import kotlin.collections.set
import org.slf4j.LoggerFactory

class StackOverflowClient(val stackAppKey: String? = null) {
    companion object {
        val logger = LoggerFactory.getLogger(StackOverflowClient::class.java)
    }

    fun siteInfo(): List<SiteInfo> {
        val call = ApiCall("/info", stackAppKey = stackAppKey)
        logger.info("siteInfo: ${call.url()}")

        return RestClient.get<SiteInfos>(call.url()).items
    }

    fun excerptSearch(freeText: String): List<SearchResultExcerpt> {
        val call = ApiCall("/search/excerpts", stackAppKey = stackAppKey).apply {
            params["order"] = "desc"
            params["sort"] = "relevance"
            params["q"] = freeText
        }
        logger.info("excerptSearch: ${call.url()}")

        return RestClient.get<SearchResults>(call.url()).items
    }

    fun answers(questionId: Int): List<Answer> {
        val call = ApiCall("/questions/$questionId/answers", stackAppKey = stackAppKey).apply {
            params["order"] = "desc"
            params["sort"] = "votes"
        }
        logger.info("answers: ${call.url()}")

        return RestClient.get<Answers>(call.url()).items
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

    fun url() = URI(API_ROOT + path + paramsString()).toURL().toString()

    private fun paramsString() =
        when (params.isEmpty()) {
            true -> ""
            false -> "?" + params.map {
                "${it.key.urlEncode()}=${it.value.urlEncode()}"
            }.joinToString("&")
        }
}
