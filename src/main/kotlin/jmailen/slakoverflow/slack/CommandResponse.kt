package jmailen.slakoverflow.slack

data class CommandResponse(val response_type: String, val text: String) {
    val attachments: MutableList<CommandResponseAttachment> = mutableListOf()

    init {
        attachments += CommandResponseAttachment(text)
    }
}

data class CommandResponseAttachment(val text: String)
