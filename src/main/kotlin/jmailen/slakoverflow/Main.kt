package jmailen.slakoverflow

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.content.HttpStatusCodeContent
import io.ktor.content.TextContent
import io.ktor.features.StatusPages
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.path
import io.ktor.request.receiveParameters
import io.ktor.request.uri
import io.ktor.request.userAgent
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import jmailen.java.mask
import jmailen.serialization.Json
import jmailen.slakoverflow.slack.CommandResponse
import jmailen.slakoverflow.slack.ResponseType
import jmailen.slakoverflow.slack.SlackClient
import jmailen.slakoverflow.stackoverflow.StackOverflowClient
import java.rmi.UnexpectedException

const val ENV_PORT = "PORT"
const val ENV_STACKAPPKEY = "STACKAPP_KEY"
const val ENV_SENTRY_DSN = "SENTRY_DSN"
const val DEFAULT_PORT = 8777

fun main(args: Array<String>) {
    val port = getPort()

    val server = embeddedServer(Netty, port) {
        install(StatusPages) {
            exception<Throwable> { cause ->
                Logger.error("request failed", cause, mapOf(
                    "path" to call.request.path(),
                    "userAgent" to (call.request.userAgent() ?: "unspecified")
                ))
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
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
            get("/testError") {
                throw UnexpectedException("we did actually expect that")
            }
        }
    }

    Logger.info("starting slakoverflow:$port")

    getStackAppKey()?.let { Logger.info("stack app key set: ${it.mask()}") }
    getSentryDSN()?.let { Logger.info("sentry dsn ${it.mask()}") }
    server.start(wait = true)
}

val bot = SlakOverflowBot(StackOverflowClient(getStackAppKey()), SlackClient())

suspend fun handleRoot(call: ApplicationCall) {
    call.respondText("slakoverflow up\n")
}

suspend fun handleSiteInfo(call: ApplicationCall) {
    val siteInfo = StackOverflowClient().siteInfo()
    call.respondJson(siteInfo)
}

suspend fun handleCommandOverflow(call: ApplicationCall) {
    val form = call.receiveParameters()
    val user = form["user_name"] ?: "you"
    val query = form["text"] ?: ""
    val responseUrl = form["response_url"]

    Logger.info("{} called by {}", call.request.uri, user)

    // acknowledge command
    call.respondJson(CommandResponse("ok $user, searching for that...", ResponseType.in_channel))

    if (responseUrl != null) {
        // delayed response
        bot.answerQuestion(user, query, responseUrl)
    } else {
        Logger.error("no response_url provided for query")
    }
}

suspend fun ApplicationCall.respondOk() {
    respond(HttpStatusCodeContent(HttpStatusCode.OK))
}

suspend fun ApplicationCall.respondJson(obj: Any) {
    val objSer = Json.write(obj)
    respond(TextContent(objSer, ContentType.Application.Json))
}

fun getPort() = System.getenv(ENV_PORT)?.toIntOrNull() ?: DEFAULT_PORT

fun getStackAppKey() = System.getenv(ENV_STACKAPPKEY)

fun getSentryDSN() = System.getenv(ENV_SENTRY_DSN)
