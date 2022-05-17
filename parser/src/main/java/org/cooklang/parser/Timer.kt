package org.cooklang.parser

import org.cooklang.parser.DirectionItem

data class Timer(
    var units: String? = null,
    var name: String? = null,
    var quantityFloat: Float? = null,
    var quantityString: String? = null
) : DirectionItem {
    override fun getDisplayString(): String {
        return "$name ${quantityFloat?: quantityString?: ""} ${units ?: ""}"
    }
}