package jmailen.slakoverflow.stackoverflow

import jmailen.jsoup.DocumentProvider
import org.jsoup.nodes.Document

class QuestionPage(val questionId: Int, documentProvider: DocumentProvider = DocumentProvider()) {

    val page: Document

    init {
        page = documentProvider.documentAt(url())
    }

    fun url() = "http://stackoverflow.com/questions/$questionId"

    fun answerUrl(answerId: Int) = "http://stackoverflow.com/a/$answerId"

    fun questionPostHtml() =
            page.select("#question .post-text").first().html().trim()

    fun answerPostHtml(answerId: Int) =
            page.select("#answer-$answerId .post-text").first().html().trim()
}
