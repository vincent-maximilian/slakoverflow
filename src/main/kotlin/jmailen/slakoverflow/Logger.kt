package jmailen.slakoverflow

import io.sentry.Sentry
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import java.util.logging.LogManager

object Logger {
    const val ENV_RELEASE = "GIT_SHA"

    init {
        LogManager.getLogManager().readConfiguration(
            javaClass.getResourceAsStream("/logging.properties")
        )
        Sentry.init().apply {
            release = System.getenv(ENV_RELEASE) ?: "dev"
            mdcTags = setOf("path", "userAgent", "user")
        }
    }
    private val slf4j = LoggerFactory.getLogger("slakoverflow")

    fun debug(message: String, vararg params: Any = arrayOf()) {
        slf4j.debug(message, *params)
    }

    fun info(message: String, vararg params: Any = arrayOf()) {
        slf4j.info(message, *params)
    }

    fun error(message: String, exception: Throwable? = null, tags: Map<String, String>? = null) {
        tags?.let { it.forEach { t, tv -> MDC.put(t, tv) } }
        slf4j.error(message, exception)
        MDC.clear()
    }
}
