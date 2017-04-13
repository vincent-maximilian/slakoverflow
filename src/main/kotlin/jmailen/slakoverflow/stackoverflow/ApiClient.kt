package jmailen.slakoverflow.stackoverflow

import com.steamstreet.krest.get
import java.net.URI
import java.net.URLEncoder

class ApiClient {
    fun siteInfo(): SiteInfo {
        val u = overflowCall("/info").uri()
        return u.get {}.response<SiteInfo>().body
    }

    fun excerptSearch(freeText: String): String {
        return ""
    }

    private fun overflowCall(path: String) =
            ApiCall().withPath(path).withParam("site", "stackoverflow")
}

class ApiCall {
    companion object {
        const val API_ROOT = "https://api.stackexchange.com/2.2"
    }

    var path = ""
    var params = mutableMapOf<String, String>()

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
