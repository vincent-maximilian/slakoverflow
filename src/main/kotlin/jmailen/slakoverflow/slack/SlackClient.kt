package jmailen.slakoverflow.slack

import jmailen.http.RestClient

class SlackClient {
    fun respond(message: CommandResponse, toUrl: String) = RestClient.post<String>(toUrl, message)
}
