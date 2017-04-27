package jmailen.jsoup

import org.jsoup.Jsoup
import java.net.URL

class DocumentProvider {
    fun documentAt(url: String, timeoutMillis: Int = 5000) =
            Jsoup.parse(URL(url), timeoutMillis)
}
