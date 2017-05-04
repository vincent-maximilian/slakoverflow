package jmailen.slakoverflow.stackoverflow

import jmailen.slakoverflow.test.IntegrationTest
import org.amshove.kluent.`should contain`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.experimental.categories.Category

@Category(IntegrationTest::class)
class QuestionPageTest : Spek({

    describe("question html page") {
        val subject = QuestionPage(testQuestionId)

        it("provides a link to the question") {
            subject.url() `should contain` "$testQuestionId"
        }

        it("gets the question html") {
            subject.questionPostHtml() `should contain` expectedQuestionHtmlPart
        }

        it("provides a link to an answer") {
            subject.answerUrl(testAnswerId) `should contain` "$testAnswerId"
        }

        it("gets an answer html") {
            subject.answerPostHtml(testAnswerId) `should contain` expectedAnswerHtmlPart
        }
    }
})

const val testQuestionId = 16336500
const val testAnswerId = 16336507

const val expectedQuestionHtmlPart =
"""<p>What is the equivalent of this expression in Kotlin?</p>"""

const val expectedAnswerHtmlPart =
"""<p>In Kotlin, <code>if</code> statements are expressions. So the following code is equivalent:</p>"""
