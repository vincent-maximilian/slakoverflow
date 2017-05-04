package jmailen.slakoverflow.stackoverflow

import org.jetbrains.spek.api.Spek
import org.junit.Assert.assertEquals

class ApiCallTest : Spek({

    test("root") {
        ApiCall() `has uri ending in` "?site=stackoverflow"
    }

    test("path") {
        ApiCall("/some/path", "a") `has uri ending in` "/some/path?site=a"
    }

    test("one param") {
        ApiCall("/path").withParam("name", "value") `has uri ending in` "/path?site=stackoverflow&name=value"
    }

    test("multiple params") {
        val call = ApiCall("/path", "a").withParam("p1", "v1").withParam("p2", "v2")
        call `has uri ending in` "/path?site=a&p1=v1&p2=v2"
    }

    test("param url encoding") {
        val call = ApiCall("/path", "a").withParam(""" n&m= """, """ultimate "funtime"?""")
        call `has uri ending in` "/path?site=a&+n%26m%3D+=ultimate+%22funtime%22%3F"
    }
})

infix fun ApiCall.`has uri ending in`(end: String) =
        assertEquals(this.uri().toString(), ApiCall.API_ROOT + end)
