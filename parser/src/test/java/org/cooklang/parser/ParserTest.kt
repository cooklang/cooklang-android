package org.cooklang.parser

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ParserTest {

    @Test
    fun testParser() {
        val recipe = Parser.parseRecipe("Slice @bacon{1} and things")
        assert(recipe!!.metadata!!.isNotEmpty())
    }
}