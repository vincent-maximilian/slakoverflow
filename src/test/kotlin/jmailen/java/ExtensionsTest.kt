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

        it("keeps short strings") {
            "Hi!".limit(5) `should equal` "Hi!"
        }

        it("keeps strings at the limit") {
            "Cool.".limit(5) `should equal` "Cool."
        }

        it("truncates long strings") {
            "A really long thing".limit(5) `should equal` "A rea..."
        }
    }

    describe("String urlEncode") {

        it("encodes URL characters") {
            "this needs=encoding?".urlEncode() `should equal` "this+needs%3Dencoding%3F"
        }
    }
})
