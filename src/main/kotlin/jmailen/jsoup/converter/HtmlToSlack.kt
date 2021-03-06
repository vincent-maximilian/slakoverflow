package jmailen.jsoup.converter

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode

/**
 * Conversion of HTML to Slack markup
 */

fun convertToSlack(html: String) = convertToSlack(Jsoup.parseBodyFragment(html))

fun convertToSlack(doc: Document) = convertNode(doc.body())

private fun convertNode(n: Node) = when {
    n is TextNode -> convertText(n)
    n.nodeName() == "a" -> convertLink(n)
    n.nodeName() == "pre" -> convertPreformatted(n)
    n.nodeName() == "code" -> "`${convertParentNode(n)}`"
    n.nodeName() == "del" -> "~${convertParentNode(n)}~"
    n.nodeName() in boldTypes -> "*${convertParentNode(n)}*"
    n.nodeName() in italTypes -> "_${convertParentNode(n)}_"
    n.nodeName() in blockTypes -> convertParentNode(n) + "\n"
    else -> convertParentNode(n)
}

private fun convertParentNode(n: Node): String {
    return n.childNodes().map { convertNode(it) }.joinToString("")
}

private fun convertLink(n: Node): String {
    var href = n.attr("href")
    var linkText = convertParentNode(n)
    return "<$href|$linkText>"
}

private fun convertPreformatted(n: Node): String {
    // handle special case of one <code> nested inside a <pre>
    val content = if (n.childNodes().size == 1 && n.childNodes().first().nodeName() == "code") {
        childHtmls(n.childNodes().first())
    } else {
        childHtmls(n)
    }
    return "```\n$content\n```"
}

private fun childHtmls(n: Node): String {
    return n.childNodes().map { it.outerHtml() }.joinToString("")
}

private fun convertText(t: TextNode) = t.text()
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")

val boldTypes = arrayOf("b", "strong")
val italTypes = arrayOf("i", "em")
val blockTypes = arrayOf("p", "div")
