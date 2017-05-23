package jmailen.slakoverflow

import jmailen.slakoverflow.serialization.Json
import jmailen.slakoverflow.slack.SlackClient
import jmailen.slakoverflow.stackoverflow.StackOverflowClient
import org.jetbrains.ktor.application.ApplicationCall
import org.jetbrains.ktor.application.call
import org.jetbrains.ktor.content.TextContent
import org.jetbrains.ktor.host.embeddedServer
import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.netty.Netty
import org.jetbrains.ktor.request.uri
import org.jetbrains.ktor.response.respondText
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.post
import org.jetbrains.ktor.routing.routing
import org.jetbrains.ktor.util.ValuesMap
import org.slf4j.LoggerFactory

const val DEFAULT_PORT = 8777
val logger = LoggerFactory.getLogger("slakoverflow")

fun main(args: Array<String>) {
    val port = getPort()
    val server = embeddedServer(Netty, port) {
        routing {
            get("/") {
                handleRoot(call)
            }
            get("/siteinfo") {
                handleSiteInfo(call)
            }
            post("/slack/command/overflow") {
                handleCommandOverflow(call)
            }
        }
    }
    logger.info("starting slakoverflow:$port")
    server.start(wait = true)
}

val bot = SlakOverflowBot(StackOverflowClient(), SlackClient())

suspend fun handleRoot(call: ApplicationCall) {
    call.respondText("slakoverflow up\n")
}

suspend fun handleSiteInfo(call: ApplicationCall) {
    val siteInfo = StackOverflowClient().siteInfo()
    call.respond(jsonResponse(siteInfo))
}

suspend fun handleCommandOverflow(call: ApplicationCall) {
    val form = call.request.receive(ValuesMap::class)
    val user = form["user_name"] ?: "you"
    val query = form["text"] ?: ""
    val responseUrl = form["response_url"]

    logger.info("{} called by {}", call.request.uri, user)

    // acknowledge command
    call.respondText("")

    if (responseUrl != null) {
        bot.answerQuestion(user, query, responseUrl)
    } else {
        logger.error("no response_url provided for query")
    }
}

fun jsonResponse(obj: Any): TextContent {
    val objSer = Json.write(obj)
    return TextContent(objSer, ContentType.Application.Json)
}

fun getPort(): Int {
    return System.getenv("PORT")?.toIntOrNull() ?: DEFAULT_PORT
}
