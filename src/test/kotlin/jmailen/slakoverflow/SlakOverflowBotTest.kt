package jmailen.slakoverflow

import jmailen.slakoverflow.slack.ResponseType
import jmailen.slakoverflow.stackoverflow.Answer
import jmailen.slakoverflow.stackoverflow.ApiClient
import jmailen.slakoverflow.stackoverflow.SearchExcerpt
import jmailen.slakoverflow.stackoverflow.SearchResultType
import org.amshove.kluent.*
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith

@RunWith(JUnitPlatform::class)
class SlakOverflowBotTest : Spek({

    describe("answerQuestion") {
        val apiMock by memoized { mock(ApiClient::class) }
        val subject by memoized { SlakOverflowBot(apiMock) }

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
            When calling apiMock.excerptSearch(question) `it returns` listOf<SearchExcerpt>()
            val result = subject.answerQuestion("astley", question)

            it("responds by saying no one has answered") {
                result.text `should equal` "ok astley, sorry no one has answered that question!"
            }

            it("responds to channel") {
                result.response_type `should equal` ResponseType.in_channel
            }
        }

        given("a set of question results") {
            When calling apiMock.excerptSearch(any()) `it returns`
                    listOf(SearchExcerpt(1, SearchResultType.question, 2, 2, true, true, "Why 1", "Don't know why", "why why why"),
                            SearchExcerpt(2, SearchResultType.question, 5, 5, true, true, "Why 2", "Maybe why", "that's why"),
                            SearchExcerpt(3, SearchResultType.question, 10, 10, false, false, "Why 3", "You don't know", "whatever"))
            When calling apiMock.answers(2) `it returns`
                    listOf(Answer(47, true, 10), Answer(30, false, 5))
            val result = subject.answerQuestion("kenny", "did you carelessly whisper?")

            it("renders the best question") {
                result.text `should contain` "Why 2"
            }

            it("renders the best answer") {
                result.text `should contain` "47"
            }

            it("responds to the channel") {
                result.response_type `should equal` ResponseType.in_channel
            }
        }
    }
})
