package jmailen.slakoverflow

import com.steamstreet.krest.get
import jmailen.slakoverflow.serialization.Json
import jmailen.slakoverflow.slack.CommandResponse
import jmailen.slakoverflow.slack.ResponseType
import jmailen.slakoverflow.stackoverflow.SiteInfo
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
import java.net.URI

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
    val response = URI("https://api.stackexchange.com/2.2/info?site=stackoverflow").get {}.response<SiteInfo>()
    call.respond(jsonResponse(response.body))
}

suspend fun handleCommandOverflow(call: ApplicationCall) {
    val form = call.request.receive(ValuesMap::class)

    logger.info("{} called by {}", call.request.uri, form["user_name"])

    val response = CommandResponse("ok then: ${form["user_name"]} you said ${form["text"]}", ResponseType.in_channel)
    call.respond(jsonResponse(response))
}

fun jsonResponse(obj: Any): TextContent {
    val objSer = Json.write(obj)
    return TextContent(objSer, ContentType.Application.Json)
}
