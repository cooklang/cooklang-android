package org.cooklang.parser

import org.cooklang.parser.Recipe

object Parser {
    external fun parseRecipe(text: String?): Recipe?

    init {
        System.loadLibrary("cooklang")
    }
}