package jmailen.slakoverflow

import com.steamstreet.krest.get
import org.jetbrains.ktor.application.ApplicationCall
import org.jetbrains.ktor.application.call
import org.jetbrains.ktor.host.embeddedServer
import org.jetbrains.ktor.netty.Netty
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.routing
import java.net.URI

fun main(args: Array<String>) {
    val server = embeddedServer(Netty, 8777) {
        routing {
            get("/") {
                handleRoot(call)
            }
        }
    }
    server.start(wait = true)
}

suspend fun handleRoot(call: ApplicationCall) {
    val response = URI("https://api.stackexchange.com/2.2/info?site=stackoverflow").get {}.response<SiteInfo>()
    call.respond("ok overflow site: ${response.body}")
}

data class SiteInfo(val items: ArrayList<SiteInfoItem>, val quota_max: Int, val quota_remaining: Int, val has_more: Boolean)

// {new_active_users=13, total_users=6967319, badges_per_minute=4.87, total_badges=22245255, total_votes=98844563, total_comments=67374329, answers_per_minute=4.71, questions_per_minute=2.99, total_answers=21545297, total_accepted=7385749, total_unanswered=3863773, total_questions=13656334, api_revision=2017.4.7.25426
data class SiteInfoItem(val new_active_users: Int,
                        val total_users: Int,
                        val total_questions: Int,
                        val total_answers: Int,
                        val total_accepted: Int,
                        val total_unanswered: Int)
