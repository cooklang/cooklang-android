package org.cooklang.parser

import org.cooklang.parser.DirectionItem

data class TextItem(var value: String? = null) : DirectionItem{
    override fun getDisplayString(): String {
        return value ?: ""
    }
}