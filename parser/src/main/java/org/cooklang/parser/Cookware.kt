package org.cooklang.parser

import org.cooklang.parser.DirectionItem

data class Cookware(
    var name: String? = null,
    var quantityFloat: Float? = null,
    var quantityString: String? = null
) : DirectionItem {
    override fun getDisplayString(): String {
        return "${quantityFloat ?: quantityString ?: ""} $name"
    }
}