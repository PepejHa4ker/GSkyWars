package com.pepej.gskywars.model.serialization

import com.pepej.papi.gson.GsonProvider
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement

object Serializer {

    inline fun <reified T> serialize(t: T): JsonElement {
        return Json.encodeToJsonElement(t)
    }

    inline fun <reified T> deserialize(json: String): T {
        val gson = GsonProvider.parser().parse(json).toString()
        return Json.decodeFromString(gson)
    }
}