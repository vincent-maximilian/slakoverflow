package jmailen.java

import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith

@RunWith(JUnitPlatform::class)
class ExtensionsTest : Spek({

    describe("String limit") {

        class tc(val label: String, val subject: String, val limit: Int, val expected: String)

        arrayOf(tc(label = "keeps short strings",           subject = "Hi!",                 limit = 5, expected = "Hi!"),
                tc(label = "keeps strings at the limit",    subject = "Cool.",               limit = 5, expected = "Cool."),
                tc(label = "truncates long strings",        subject = "A really long thing", limit = 5, expected = "A rea..."),
                tc(label = "truncates all with zero limit", subject = "Something",           limit = 0, expected = "...")
        ).forEach { with(it) {

            it(label) {
                subject.limit(limit) `should equal` expected
            }
        }}
    }

    describe("String urlEncode") {

        it("encodes URL characters") {
            "this needs=encoding?".urlEncode() `should equal` "this+needs%3Dencoding%3F"
        }
    }
})
