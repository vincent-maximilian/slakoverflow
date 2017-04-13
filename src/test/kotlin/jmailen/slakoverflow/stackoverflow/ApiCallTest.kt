package jmailen.slakoverflow.stackoverflow

import org.hamcrest.CoreMatchers.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ApiCallTest {
    lateinit var call: ApiCall

    @Before
    fun setup() {
        call = ApiCall()
    }

    @Test
    fun testRoot() {
        assertThat(callUri(), isUriEndingIn())
    }

    @Test
    fun testPath() {
        call = call.withPath("/some/path")
        assertThat(callUri(), isUriEndingIn("/some/path"))
    }

    @Test
    fun testOneParam() {
        call = call.withPath("/path").withParam("name", "value")
        assertThat(callUri(), isUriEndingIn("/path?name=value"))
    }

    @Test
    fun testMultipleParams() {
        call = call.withPath("/path").withParam("p1", "v1").withParam("p2", "v2")
        assertThat(callUri(), isUriEndingIn("/path?p1=v1&p2=v2"))
    }

    @Test
    fun testParamUrlEncoding() {
        call = call.withPath("/path").withParam(""" n&m= """, """ultimate "funtime"?""")
        assertThat(callUri(), isUriEndingIn("/path?+n%26m%3D+=ultimate+%22funtime%22%3F"))
    }

    fun callUri() = call.uri().toString()

    fun isUriEndingIn(end: String = "") = equalTo(ApiCall.API_ROOT + end)
}
