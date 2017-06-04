package jmailen.slakoverflow.slack

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Request
import jmailen.slakoverflow.serialization.Json
import org.slf4j.LoggerFactory

class SlackClient {

    val logger = LoggerFactory.getLogger("SlackClient")

    fun respond(message: CommandResponse, toUrl: String) {
        Fuel.post(toUrl).json(message).response { _, r, _ ->
            when (r.httpStatusCode) {
                in 200..299 -> logger.info("sent response to: $toUrl")
                in 300..599 -> logger.error("response to: $toUrl failed with ${r.httpStatusCode}")
                else -> logger.warn("response to: $toUrl unexpected code ${r.httpStatusCode}")
            }
        }
    }
}

fun Request.json(obj: Any) =
        header("Content-type" to "application/json")
                .body(Json.write(obj))
