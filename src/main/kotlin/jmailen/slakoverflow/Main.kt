package jmailen.slakoverflow

import com.steamstreet.krest.get
import org.jetbrains.ktor.application.call
import org.jetbrains.ktor.host.embeddedServer
import org.jetbrains.ktor.netty.Netty
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.routing
import java.net.URI

fun main(args: Array<String>) {
    val result = URI("https://api.stackexchange.com/2.2/info?site=stackoverflow").get {}.response<Map<String, Any>>().body
    println("stack overflow says: $result")

    val server = embeddedServer(Netty, 8777) {
        routing {
            get("/") {
                call.respond("ok overflow $result")
            }
        }
    }
    server.start(wait = true)
}
