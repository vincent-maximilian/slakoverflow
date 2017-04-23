package jmailen.java

import java.net.URLEncoder

fun String.limit(size: Int): String {
    return when (this.length > size) {
        true -> this.subSequence(0, size).toString() + "..."
        false -> this
    }
}

fun String.urlEncode() = URLEncoder.encode(this, "UTF-8")
