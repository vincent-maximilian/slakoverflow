package jmailen.slakoverflow.stackoverflow

import com.steamstreet.krest.get
import org.slf4j.LoggerFactory
import java.net.URI
import java.net.URLEncoder

class ApiClient {
    companion object {
        val logger = LoggerFactory.getLogger(ApiClient::class.java)
    }

    fun siteInfo(): List<SiteInfo> {
        val u = ApiCall("/info").uri()
        logger.info("siteInfo: $u")
        return u.get().response<SiteInfos>().body.items
    }

    fun excerptSearch(freeText: String): List<SearchExcerpt> {
        val u = ApiCall("/search/excerpts")
                .withParam("order", "desc")
                .withParam("sort", "votes")
                .withParam("q", freeText).uri()
        logger.info("excerptSearch: $u")
        return u.get().response<SearchExcerpts>().body.items
    }
}

class ApiCall(val path: String = "", site: String = ApiCall.STACKOVERFLOW_SITE) {

    companion object {
        const val API_ROOT = "https://api.stackexchange.com/2.2"
        const val STACKOVERFLOW_SITE = "stackoverflow"
    }

    var params = LinkedHashMap<String, String>()

    init {
        params.set("site", site)
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

fun String.urlEncode() = URLEncoder.encode(this, "UTF-8")

typealias AnyJson = Map<String, Any>