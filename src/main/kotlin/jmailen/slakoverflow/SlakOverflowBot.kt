package jmailen.slakoverflow

import jmailen.java.limit
import jmailen.slakoverflow.slack.CommandResponse
import jmailen.slakoverflow.slack.ResponseType
import jmailen.slakoverflow.stackoverflow.Answer
import jmailen.slakoverflow.stackoverflow.ApiClient
import jmailen.slakoverflow.stackoverflow.SearchExcerpt
import jmailen.slakoverflow.stackoverflow.SearchResultType

class SlakOverflowBot(val overflowApi: ApiClient) {

    fun answerQuestion(user: String, question: String): CommandResponse {
        if (question != null && question.trim() != "") {
            val question = bestQuestion(question)

            if (question != null) {
                val answer = bestAnswer(question.question_id)
                return answerResponse(user, question, answer)
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

    private fun answerResponse(user: String, question: SearchExcerpt, answer: Answer): CommandResponse {
        var text = "ok $user, found *<http://stackoverflow.com/questions/${question.question_id}|Question>" +
                " (${question.score} votes)*:" +
                " *${question.title}*"
        text += "\n>>>\n${question.excerpt.limit(1000)}\n\n"
        text += "*<http://stackoverflow.com/a/${answer.answer_id}|Answer> (${answer.score} votes):*\n"
        return CommandResponse(text, ResponseType.in_channel)
    }
}
