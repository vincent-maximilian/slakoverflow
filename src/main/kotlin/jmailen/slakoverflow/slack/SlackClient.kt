package jmailen.slakoverflow.slack

import com.steamstreet.krest.post
import java.net.URI

class SlackClient {

    fun respond(response: CommandResponse, toUrl: String) {
        URI(toUrl).post { json(response) }.execute()
    }
}
