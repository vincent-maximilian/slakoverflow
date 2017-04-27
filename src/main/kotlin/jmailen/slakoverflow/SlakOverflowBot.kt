package jmailen.slakoverflow

import jmailen.java.limit
import jmailen.slakoverflow.slack.CommandResponse
import jmailen.slakoverflow.slack.ResponseType
import jmailen.slakoverflow.stackoverflow.*

class SlakOverflowBot(val overflowApi: ApiClient) {

    fun answerQuestion(user: String, question: String): CommandResponse {
        if (question.trim() != "") {
            val result = bestQuestion(question)

            if (result != null) {
                val answer = bestAnswer(result.question_id)
                return answerResponse(user, result, answer)
            } else {
                return CommandResponse("ok $user, sorry no one has answered that question!", ResponseType.in_channel)
            }
        } else {
            return CommandResponse("ok $user, did you have a question?", ResponseType.ephemeral)
        }
    }

    private fun bestQuestion(query: String) =
            overflowApi.excerptSearch(query)
                    .filter { it.item_type == SearchResultType.question && it.is_answered }
                    .sortedBy { it.score * 10 + it.question_score }
                    .lastOrNull()

    private fun bestAnswer(questionId: Int) = overflowApi.answers(questionId).first()

    private fun answerResponse(user: String, question: SearchResultExcerpt, answer: Answer): CommandResponse {
        val page = QuestionPage(question.question_id)

        var text = "ok $user, found *<${page.url()}|${question.title}> (${question.score} votes)*:\n"
        text += ">>>\n${page.questionPostHtml().limit(1000)}\n\n"

        text += "*<${page.answerUrl(answer.answer_id)}|Answer> (${answer.score} votes):*\n"
        text += "\n${page.answerPostHtml(answer.answer_id).limit(1000)}\n\n"

        return CommandResponse(text, ResponseType.in_channel)
    }
}
