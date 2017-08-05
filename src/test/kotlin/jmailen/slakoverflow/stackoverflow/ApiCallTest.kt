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
        ApiCall("/path").apply {
            params["name"] = "value"
        } `has uri ending in` "/path?site=stackoverflow&name=value"
    }

    test("multiple params") {
        ApiCall("/path", "a").apply {
            params["p1"] = "v1"
            params["p2"] = "v2"
        } `has uri ending in` "/path?site=a&p1=v1&p2=v2"
    }

    test("param url encoding") {
        ApiCall("/path", "a").apply {
            params[""" n&m= """] = """ultimate "funtime"?"""
        } `has uri ending in` "/path?site=a&+n%26m%3D+=ultimate+%22funtime%22%3F"
    }

    test("api app key") {
        ApiCall("/path", stackAppKey = "mykey").apply {
            params["p1"] = "v1"
        } `has uri ending in` "/path?site=stackoverflow&key=mykey&p1=v1"
    }
})

infix fun ApiCall.`has uri ending in`(end: String) =
        assertEquals(this.uri().toString(), ApiCall.API_ROOT + end)
