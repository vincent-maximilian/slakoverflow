package jmailen.http

import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache

object Http {
    val client = HttpClient(Apache)
}
