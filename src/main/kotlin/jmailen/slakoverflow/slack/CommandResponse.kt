package jmailen.slakoverflow.slack

/**
 * Response to a Slack slash command
 */
data class CommandResponse(
    val text: String,
    val response_type: ResponseType = ResponseType.ephemeral,
    val attachments: MutableList<CommandResponseAttachment> = mutableListOf()
) {

    fun attach(content: String) {
        attachments += CommandResponseAttachment(content)
    }
}

data class CommandResponseAttachment(val text: String)

enum class ResponseType {
    in_channel,
    ephemeral
}
