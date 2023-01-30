@file:Suppress("unused")

package com.example.namiokai.utils

import org.json.JSONArray
import org.json.JSONObject

class JsonBuilder {

    private val jsonArray = JSONArray()

    companion object {
        private const val INDENT_SPACES = 4
    }

    fun append(jsonObject: JSONObject) {
        jsonArray.put(jsonObject)
    }

    fun append(map: Map<String, Any>) {
        jsonArray.put(JSONObject(map))
    }

    override fun toString(): String {
        return jsonArray.toString(INDENT_SPACES)
    }

}