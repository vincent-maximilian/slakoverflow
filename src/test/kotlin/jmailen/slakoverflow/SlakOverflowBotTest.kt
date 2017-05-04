package jmailen.slakoverflow

import jmailen.slakoverflow.slack.ResponseType
import jmailen.slakoverflow.stackoverflow.*
import org.amshove.kluent.*
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it

class SlakOverflowBotTest : Spek({

    describe("answerQuestion") {
        val stackOverflow by memoized { mock(Client::class) }
        val questionPage by memoized { mock(QuestionPage::class) }
        val subject by memoized { SlakOverflowBot(stackOverflow) }

        given("empty question") {
            val result = subject.answerQuestion("yanni", " ")

            it("responds by asking you if you have a question") {
                result.text `should equal` "ok yanni, did you have a question?"
            }

            it("responds privately") {
                result.response_type `should equal` ResponseType.ephemeral
            }
        }

        given("no results for question") {
            val question = "will I ever give you up?"
            When calling stackOverflow.excerptSearch(question) `it returns` listOf<SearchResultExcerpt>()
            val result = subject.answerQuestion("astley", question)

            it("responds by saying no one has answered") {
                result.text `should equal` "ok astley, sorry no one has answered that question!"
            }

            it("responds to channel") {
                result.response_type `should equal` ResponseType.in_channel
            }
        }

        given("a set of question results") {
            When calling stackOverflow.excerptSearch(any()) `it returns`
                    listOf(SearchResultExcerpt(1, SearchResultType.question, 2, 2, true, true, "Why 1", "Don't know why", "why why why"),
                            SearchResultExcerpt(2, SearchResultType.question, 5, 5, true, true, "Why 2", "Maybe why", "that's why"),
                            SearchResultExcerpt(3, SearchResultType.question, 10, 10, false, false, "Why 3", "You don't know", "whatever"))
            When calling stackOverflow.answers(2) `it returns` listOf(Answer(47, true, 10), Answer(30, false, 5))
            When calling stackOverflow.pageFor(2) `it returns` questionPage
            When calling questionPage.url() `it returns` "http://stackoverflow.com/questions/2"
            When calling questionPage.answerUrl(47) `it returns` "http://stackoverflow.com/a/47"
            When calling questionPage.questionPostHtml() `it returns` "Have you played <b>Careless Whisper</b> yes?"
            When calling questionPage.answerPostHtml(47) `it returns` "Yes and I'll be <i>Forever in Love</i>."

            val result = subject.answerQuestion("kenny", "did you carelessly whisper?")

            it("includes a link to the best question") {
                result.text `should contain` "http://stackoverflow.com/questions/2"
            }

            it("renders the best question") {
                result.text `should contain` "<b>Careless Whisper</b>"
            }

            it("includes a link to the best answer") {
                result.text `should contain` "http://stackoverflow.com/a/47"
            }

            it("renders the best answer") {
                result.text `should contain` "<i>Forever in Love</i>"
            }

            it("responds to the channel") {
                result.response_type `should equal` ResponseType.in_channel
            }
        }
    }
})
