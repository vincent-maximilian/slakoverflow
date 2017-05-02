package jmailen.slakoverflow.serialization

import org.amshove.kluent.`should contain`
import org.amshove.kluent.`should equal`
import org.amshove.kluent.`should not contain`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith

@RunWith(JUnitPlatform::class)
class JsonTest : Spek({

    describe("json marshalling") {
        given("json data") {
            val result = Json.write(TestJsonObject(true, 5.6f, "val", listOf("that"))).toString()

            it("marshals it into json") {
                result `should contain` """"someBool":true"""
                result `should contain` """"someNum":5.6"""
                result `should contain` """"someStr":"val""""
                result `should contain` """"someArray":["that"]"""
            }

            it("skips null keys") {
                result `should not contain` "someObj"
            }
        }
    }

    describe("json unmarshalling") {
        given("any json object") {
            val result: AnyJson = Json.read(jsonObject.toByteArray())

            it("reads the data into a map") {
                result["someBool"] `should equal` true
                result["someNum"] `should equal` 1.2
                result["someStr"] `should equal` "strVal"
                result["someObj"] `should equal` mapOf("nested" to "nestedVal")
            }
        }

        given("any json object list") {
            val result: AnyJsonList = Json.read(jsonList.toByteArray())

            it("reads the data into a list of maps") {
                result[0]["key"] `should equal` "value1"
                result[1]["objectKey"] `should equal` mapOf("nestedKey" to "nestedvalue2")
            }
        }

        given("specific json object") {
            val result: TestJsonObject = Json.read(jsonObject.toByteArray())

            it("reads the data into the object type") {
                result `should equal` TestJsonObject(true, 1.2f, "strVal", listOf("one", "two", "three"), TestNestedJsonObject("nestedVal"))
            }
        }

        given("specific json object list") {
            val result: List<TestJsonListObject> = Json.readList(jsonList.toByteArray())

            it("reads the data into the list object type") {
                result[0].key `should equal` "value1"
            }
        }
    }
})

data class TestJsonObject(val someBool: Boolean, val someNum: Float, val someStr: String,
                          val someArray: List<String>, val someObj: TestNestedJsonObject? = null)

data class TestNestedJsonObject(val nested: String)

data class TestJsonListObject(val key: String, val objectKey: TestJsonListNestedObject)

data class TestJsonListNestedObject(val nestedKey: String)

const val jsonObject = """
{
    "someBool": true,
    "someNum": 1.2,
    "someStr": "strVal",
    "someArray": ["one", "two", "three"],
    "someObj": {
        "nested": "nestedVal"
    }
}
"""

const val jsonList = """
[
{
    "key": "value1",
    "objectKey": {
        "nestedKey": "nestedvalue1"
    }
},
{
    "key": "value2",
    "objectKey": {
        "nestedKey": "nestedvalue2"
    }
}
]
"""
