package jmailen.jsoup

import java.net.URL
import org.jsoup.Jsoup

class DocumentProvider {
    fun documentAt(url: String, timeoutMillis: Int = 5000) =
            Jsoup.parse(URL(url), timeoutMillis)
}
