package jmailen.slakoverflow.stackoverflow

import com.steamstreet.krest.get
import org.slf4j.LoggerFactory
import java.net.URI
import java.net.URLEncoder

class ApiClient {
    companion object {
        val logger = LoggerFactory.getLogger(ApiClient::class.java)
    }

    fun siteInfo(): SiteInfo {
        val u = overflowCall("/info").uri()
        logger.info("siteInfo: $u")
        return u.get {}.response<SiteInfo>().body
    }

    fun excerptSearch(freeText: String): AnyJson {
        val u = overflowCall("/search/excerpts")
                .withParam("order", "desc")
                .withParam("sort", "votes")
                .withParam("q", freeText).uri()
        logger.info("excerptSearch: $u")
        return u.get {}.response<AnyJson>().body
    }

    private fun overflowCall(path: String) =
            ApiCall().withPath(path).withParam("site", "stackoverflow")
}

class ApiCall {
    companion object {
        const val API_ROOT = "https://api.stackexchange.com/2.2"
    }

    var path = ""
    var params = LinkedHashMap<String, String>()

    fun withPath(p: String): ApiCall {
        path = p
        return this
    }

    fun withParam(name: String, value: String): ApiCall {
        params.set(name, value)
        return this
    }

    fun uri() = URI(API_ROOT + path + paramsString())

    private fun paramsString() =
            when (params.isEmpty()) {
                true -> ""
                false -> "?" + params.map { "${it.key.urlEncode()}=${it.value.urlEncode()}" }.joinToString("&")
            }
}

fun String.urlEncode() = URLEncoder.encode(this, "UTF-8")

typealias AnyJson = Map<String, Any>
