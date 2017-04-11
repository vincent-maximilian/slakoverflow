package jmailen.slakoverflow.serialization

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

object Json {
    private val mapper = jacksonObjectMapper()
    init {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false) // use ISO8601
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL) // skip keys with null values
    }

    fun write(obj: Any) = mapper.writeValueAsBytes(obj)
}
