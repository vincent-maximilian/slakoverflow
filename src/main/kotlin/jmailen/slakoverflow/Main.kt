package jmailen.slakoverflow

import jmailen.slakoverflow.serialization.Json
import jmailen.slakoverflow.stackoverflow.ApiClient
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

const val PORT = 8777
val logger = LoggerFactory.getLogger("slakoverflow")

fun main(args: Array<String>) {
    val server = embeddedServer(Netty, PORT) {
        routing {
            get("/") {
                handleRoot(call)
            }
            post("/slack/command/overflow") {
                handleCommandOverflow(call)
            }
        }
    }
    logger.info("starting slakoverflow:$PORT")
    server.start(wait = true)
}

val bot = SlakOverflowBot(ApiClient())

suspend fun handleRoot(call: ApplicationCall) {
    val siteInfo = ApiClient().siteInfo()
    call.respond(jsonResponse(siteInfo))
}

suspend fun handleCommandOverflow(call: ApplicationCall) {
    val form = call.request.receive(ValuesMap::class)
    val user = form["user_name"] ?: "you"
    val query = form["text"] ?: ""

    logger.info("{} called by {}", call.request.uri, user)

    val answer = bot.answerQuestion(user, query)

    call.respond(jsonResponse(answer))
}

fun jsonResponse(obj: Any): TextContent {
    val objSer = Json.write(obj)
    return TextContent(objSer, ContentType.Application.Json)
}
