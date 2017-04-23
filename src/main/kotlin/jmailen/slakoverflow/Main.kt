package jmailen.slakoverflow

import jmailen.java.limit
import jmailen.slakoverflow.serialization.Json
import jmailen.slakoverflow.slack.CommandResponse
import jmailen.slakoverflow.slack.ResponseType
import jmailen.slakoverflow.stackoverflow.Answer
import jmailen.slakoverflow.stackoverflow.ApiClient
import jmailen.slakoverflow.stackoverflow.SearchExcerpt
import jmailen.slakoverflow.stackoverflow.SearchResultType
import org.jetbrains.ktor.application.ApplicationCall
import org.jetbrains.ktor.application.call
import org.jetbrains.ktor.content.TextContent
import org.jetbrains.ktor.host.embeddedServer
import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.netty.Netty
import org.jetbrains.ktor.request.uri
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.post
import org.jetbrains.ktor.routing.routing
import org.jetbrains.ktor.util.ValuesMap
import org.slf4j.LoggerFactory

val logger = LoggerFactory.getLogger("slakoverflow")

fun main(args: Array<String>) {
    val server = embeddedServer(Netty, 8777) {
        routing {
            get("/") {
                handleRoot(call)
            }
            post("/slack/command/overflow") {
                handleCommandOverflow(call)
            }
        }
    }
    server.start(wait = true)
}

suspend fun handleRoot(call: ApplicationCall) {
    val siteInfo = ApiClient().siteInfo()
    call.respond(jsonResponse(siteInfo))
}

suspend fun handleCommandOverflow(call: ApplicationCall) {
    val form = call.request.receive(ValuesMap::class)
    val user = form["user_name"] ?: "you"
    val query = form["text"]

    logger.info("{} called by {}", call.request.uri, user)

    var response: CommandResponse
    if (query != null && query.trim() != "") {
        val question = bestQuestion(query)

        if (question != null) {
            val answer = bestAnswer(question.question_id)
            response = answerResponse(user, question, answer)
        } else {
            response = CommandResponse("ok $user, sorry no one has answered that question!", ResponseType.in_channel)
        }
    } else {
        response = CommandResponse("ok $user, did you have a question?", ResponseType.ephemeral)
    }
    call.respond(jsonResponse(response))
}

fun bestQuestion(query: String) =
        ApiClient().excerptSearch(query)
            .filter { it.item_type == SearchResultType.question && it.is_answered }
            .sortedBy { it.score * 10 + it.question_score }
            .lastOrNull()

fun bestAnswer(questionId: Int) = ApiClient().answers(questionId).first()

fun answerResponse(user: String, question: SearchExcerpt, answer: Answer): CommandResponse {
    var text = "ok $user, found answer to:" +
            " <http://stackoverflow.com/questions/${question.question_id}|*${question.title}*>" +
            " with ${question.score} votes.\n>>>"
    text += "*Q:*\n${question.body.limit(500)}\n\n" +
            "*A:*\nhttp://stackoverflow.com/a/${answer.answer_id}"
    return CommandResponse(text, ResponseType.in_channel)
}

fun jsonResponse(obj: Any): TextContent {
    val objSer = Json.write(obj)
    return TextContent(objSer, ContentType.Application.Json)
}
