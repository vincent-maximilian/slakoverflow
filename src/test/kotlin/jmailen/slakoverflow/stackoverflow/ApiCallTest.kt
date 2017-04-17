package jmailen.slakoverflow.stackoverflow

import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Test

class ApiCallTest {

    @Test
    fun testRoot() {
        assertThat(ApiCall().uriStr(), isUriEndingIn("?site=stackoverflow"))
    }

    @Test
    fun testPath() {
        val call = ApiCall("/some/path", "a")
        assertThat(call.uriStr(), isUriEndingIn("/some/path?site=a"))
    }

    @Test
    fun testOneParam() {
        val call = ApiCall("/path").withParam("name", "value")
        assertThat(call.uriStr(), isUriEndingIn("/path?site=stackoverflow&name=value"))
    }

    @Test
    fun testMultipleParams() {
        val call = ApiCall("/path", "a").withParam("p1", "v1").withParam("p2", "v2")
        assertThat(call.uriStr(), isUriEndingIn("/path?site=a&p1=v1&p2=v2"))
    }

    @Test
    fun testParamUrlEncoding() {
        val call = ApiCall("/path", "a").withParam(""" n&m= """, """ultimate "funtime"?""")
        assertThat(call.uriStr(), isUriEndingIn("/path?site=a&+n%26m%3D+=ultimate+%22funtime%22%3F"))
    }

    fun isUriEndingIn(end: String = "") = equalTo(ApiCall.API_ROOT + end)
}

fun ApiCall.uriStr() = this.uri().toString()