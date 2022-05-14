package org.cooklang.parser

import java.lang.StringBuilder
import java.util.*

class Recipe {
    var steps: MutableList<Step> = mutableListOf()
    var metadata = Hashtable<String, String>()
    fun addStep(step: Step) {
        steps.add(step)
    }

    fun addMetadata(key: String, value: String) {
        metadata[key] = value
    }

    // maybe move to method other than toString()
    override fun toString(): String {
        if (steps.size <= 0) return ""
        val sb = StringBuilder()
        for ((key, value) in metadata) {
            sb.append(String.format(">> %s: %s\n", key, value))
        }
        steps.forEachIndexed { index, step ->
            sb.append(step.toString())
            if (index != steps.lastIndex){
                sb.append("\n")
            }
        }
        return sb.toString()
    }
}