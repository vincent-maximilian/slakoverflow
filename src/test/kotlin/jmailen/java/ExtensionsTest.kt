package jmailen.java

import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.data_driven.data
import org.jetbrains.spek.data_driven.on

class ExtensionsTest : Spek({

    describe("String limit") {

        on("limiting string '%s' to %s",
                data("Hi!", 5, expected = "Hi!"),
                data("Cool.", 5, expected = "Cool."),
                data("A really long thing", 5, expected = "A rea..."),
                data("Something", 0, "...")
        ) { subject, limit, expected ->

            it("outputs '$expected'") {
                subject.limit(limit) `should equal` expected
            }
        }
    }

    describe("String urlEncode") {

        it("encodes URL characters") {
            "this needs=encoding?".urlEncode() `should equal` "this+needs%3Dencoding%3F"
        }
    }
})
