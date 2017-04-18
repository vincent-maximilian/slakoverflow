package jmailen.slakoverflow

import jmailen.slakoverflow.serialization.Json
import jmailen.slakoverflow.slack.CommandResponse
import jmailen.slakoverflow.slack.ResponseType
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
    val question = form["text"]

    logger.info("{} called by {}", call.request.uri, user)

    var response: CommandResponse
    if (question != null && question.trim() != "") {
        val answer = bestSearchResult(question)

        if (answer != null) {
            response = answerResponse(user, answer)
        } else {
            response = CommandResponse("ok $user, sorry no one has answered that question!", ResponseType.in_channel)
        }
    } else {
        response = CommandResponse("ok $user, did you have a question?", ResponseType.ephemeral)
    }
    call.respond(jsonResponse(response))
}

fun bestSearchResult(query: String) =
        ApiClient().excerptSearch(query)
            .filter { it.item_type == SearchResultType.answer && it.is_accepted }
            .sortedBy { it.score * 10 + it.question_score }
            .lastOrNull()

fun answerResponse(user: String, answer: SearchExcerpt): CommandResponse {
    val response = CommandResponse("ok $user, found answer to:"
            + " <http://stackoverflow.com/questions/${answer.question_id}|*${answer.title}*>"
            + " with ${answer.score} votes.", ResponseType.in_channel)
    response.attach("${answer.body}\n...\n${answer.excerpt}")
    return response
}

fun jsonResponse(obj: Any): TextContent {
    val objSer = Json.write(obj)
    return TextContent(objSer, ContentType.Application.Json)
}
