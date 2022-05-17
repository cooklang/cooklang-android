package org.cooklang.parser

import java.lang.StringBuilder
import java.util.*

class Recipe : Displayable {
    val steps: MutableList<Step> = mutableListOf()
    var metadata = Hashtable<String, String>()
    fun addStep(step: Step) {
        steps.add(step)
    }

    val ingredients: List<Ingredient> get() = steps.map { it.ingredients }.reduce { acc, list -> acc + list }
    val cookware: List<Cookware> get() = steps.map { it.cookware }.reduce { acc, list -> acc + list }

    fun addMetadata(key: String, value: String) {
        metadata[key] = value
    }

    override fun getDisplayString(): String {
        if (steps.size <= 0) return ""
        val sb = StringBuilder()
        for ((key, value) in metadata) {
            sb.append(">> $key: $value\n")
        }
        steps.forEachIndexed { index, step ->
            sb.append("${index + 1}. ${step.getDisplayString()}")
            if (index != steps.lastIndex) {
                sb.append("\n")
            }
        }
        return sb.toString()
    }
}