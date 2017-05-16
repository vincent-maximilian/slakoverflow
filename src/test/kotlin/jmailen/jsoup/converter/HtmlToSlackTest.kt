package jmailen.jsoup.converter

import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.xit

class HtmlToSlackTest : Spek({

    describe("converting HTML to Slack markdown") {

        it("converts plain text") {
            convertToSlack(plainText) `should equal` "Just some text"
        }

        it("converts html with no formatting") {
            convertToSlack(noFormatting) `should equal` "Some stuff\n"
        }

        it("converts nested html with no formatting") {
            convertToSlack(nestedNoFormatting) `should equal` "Some stuff and more stuff\n"
        }

        it("converts html with formatting") {
            convertToSlack(formatting) `should equal` "Some *stuff* and really _stuff_ there.\n"
        }

        it("escapes html as entities") {
            convertToSlack(needsEscaping) `should equal` "this &gt; that &amp; one &lt; two\n"
        }

        it("converts html with links") {
            convertToSlack(withLinks) `should equal` "Go <http://over/here|here>\n"
        }

        xit("converts html with code blocks") {
            convertToSlack(codeBlock) `should equal` "Example\n\n" +
                    "\"\"\"\n" +
                    " 10 print \"you are the <b>best</b>\"\n" +
                    " 20 goto 10\n" +
                    "\"\"\"\n"
        }

        xit("converts html with blockquotes") {

        }

        xit("converts html with lists") {

        }
    }
})

val plainText = "Just some text"

val noFormatting = """<div id="question" class="post-text">Some stuff</div>"""

val nestedNoFormatting = """<div id="question" class="post-text"><span>Some stuff</span> and more stuff</div>"""

val formatting = """<div id="question" class="post-text">Some <b>stuff</b> and really <em>stuff</em> there.</div>"""

val needsEscaping = """<div><span>this > that & one < two</span></div>"""

val withLinks = """<div>Go <a href="http://over/here">here</a></div>"""

val codeBlock = """<div><p>Example<p><pre><code>
    10 print "you are the <b>best</b>"
    20 goto 10
</code></pre></div>"""
