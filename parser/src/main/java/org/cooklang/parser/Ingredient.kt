package org.cooklang.parser

import org.cooklang.parser.DirectionItem

data class Ingredient(
    var name: String? = null,
    var quantityFloat: Float? = null,
    var quantityString: String? = null,
    var units: String? = null //maybe should be enum?
) : DirectionItem{
    override fun getDisplayString(): String {
        return "$name"
    }
}