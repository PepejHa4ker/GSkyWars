package com.pepej.gskywars.model

import com.google.common.base.Preconditions
import com.google.gson.JsonElement
import com.pepej.papi.gson.GsonProvider
import com.pepej.papi.gson.GsonSerializable
import com.pepej.papi.gson.JsonBuilder
import com.pepej.papi.serialize.Position

data class Chest(
    val id: Int,
    val position: Position,
    val rarity: Rarity,
    val opened: Boolean = false
) : GsonSerializable {

    companion object {
        fun deserialize(element: JsonElement): Chest? {
            Preconditions.checkArgument(element.isJsonObject)
            val jObject = element.asJsonObject
            Preconditions.checkArgument(jObject.has("id"))
            Preconditions.checkArgument(jObject.has("position"))
            Preconditions.checkArgument(jObject.has("rarity"))
            return GsonProvider.standard().fromJson(element, Chest::class.java)

        }
    }


    enum class Rarity(val rarName: String) {
        COMMON("Обычный"),
        MIDDLE("Редкий"),
        MYTHIC("Мистический")

    }


    override fun serialize(): JsonElement {
        return JsonBuilder.`object`()
            .add("id", id)
            .add("position", position)
            .add("rarity", rarity.rarName)
            .add("opened", opened)
            .build()
    }

}
