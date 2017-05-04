package jmailen.slakoverflow

import jmailen.slakoverflow.serialization.Json
import jmailen.slakoverflow.stackoverflow.Client
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

val bot = SlakOverflowBot(Client())

suspend fun handleRoot(call: ApplicationCall) {
    call.respond(textResponse("slakoverflow up\n"))
}

suspend fun handleSiteInfo(call: ApplicationCall) {
    val siteInfo = Client().siteInfo()
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

fun textResponse(text: String) = TextContent(text, ContentType.Application.Any)

fun jsonResponse(obj: Any): TextContent {
    val objSer = Json.write(obj)
    return TextContent(objSer, ContentType.Text.Plain)
}

fun getPort(): Int {
    return System.getenv("PORT")?.toIntOrNull() ?: DEFAULT_PORT
}
