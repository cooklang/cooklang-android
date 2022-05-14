package org.cooklang.parser

import org.cooklang.parser.DirectionItem
import org.cooklang.parser.TextItem
import org.cooklang.parser.Ingredient
import org.cooklang.parser.Cookware
import java.lang.StringBuilder
import java.util.ArrayList

class Step {
    var directions: MutableList<DirectionItem> = mutableListOf()
    fun addTextItem(texItem: TextItem) {
        directions.add(texItem)
    }

    fun addIngredient(ingredient: Ingredient) {
        directions.add(ingredient)
    }

    fun addCookware(cookware: Cookware) {
        directions.add(cookware)
    }

    fun addTimer(timer: Timer) {
        directions.add(timer)
    }

    override fun toString(): String {
        if (directions.size <= 0) return ""
        val sb = StringBuilder()
        directions.forEach {
            sb.append(it.toString())
        }
        return sb.toString()
    }
}