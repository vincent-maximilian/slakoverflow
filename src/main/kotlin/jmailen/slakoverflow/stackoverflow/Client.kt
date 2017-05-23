package jmailen.slakoverflow.stackoverflow

import com.steamstreet.krest.get
import jmailen.java.urlEncode
import org.slf4j.LoggerFactory
import java.net.URI

class StackOverflowClient(val stackAppKey: String? = null) {
    companion object {
        val logger = LoggerFactory.getLogger(StackOverflowClient::class.java)
    }

    fun siteInfo(): List<SiteInfo> {
        val u = ApiCall("/info", stackAppKey = stackAppKey).uri()
        logger.info("siteInfo: $u")
        return u.get().response<SiteInfos>().body.items
    }

    fun excerptSearch(freeText: String): List<SearchResultExcerpt> {
        val u = ApiCall("/search/excerpts", stackAppKey = stackAppKey)
                .withParam("order", "desc")
                .withParam("sort", "relevance")
                .withParam("q", freeText).uri()
        logger.info("excerptSearch: $u")
        return u.get().response<SearchResults>().body.items
    }

    fun answers(questionId: Int): List<Answer> {
        val u = ApiCall("/questions/$questionId/answers", stackAppKey = stackAppKey)
                .withParam("order", "desc")
                .withParam("sort", "votes").uri()
        logger.info("answers: $u")
        return u.get().response<Answers>().body.items
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

    fun withParam(name: String, value: String): ApiCall {
        params.set(name, value)
        return this
    }

    fun uri() = URI(API_ROOT + path + paramsString())

    private fun paramsString() =
            when (params.isEmpty()) {
                true -> ""
                false -> "?" + params.map {
                    "${it.key.urlEncode()}=${it.value.urlEncode()}"
                }.joinToString("&")
            }
}
