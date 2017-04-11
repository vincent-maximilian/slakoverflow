package jmailen.slakoverflow.slack

data class CommandResponse(val text: String, val response_type: ResponseType = ResponseType.ephemeral) {
    val attachments: MutableList<CommandResponseAttachment> = mutableListOf()

    init {
        attachments += CommandResponseAttachment(text)
    }
}

data class CommandResponseAttachment(val text: String)

enum class ResponseType {
    in_channel,
    ephemeral
}
