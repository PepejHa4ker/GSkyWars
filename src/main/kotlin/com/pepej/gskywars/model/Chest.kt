package com.pepej.gskywars.model

import kotlinx.serialization.Serializable

@Serializable
data class Chest(
    val id: Int,
    val position: Position,
    val rarity: Rarity,
    val opened: Boolean = false
) {




    enum class Rarity(val rarName: String) {
        COMMON("Обычный"),
        MIDDLE("Редкий"),
        MYTHIC("Мистический")

    }


//    override fun serialize(): JsonElement {
//        return JsonBuilder.`object`()
//            .add("id", id)
//            .add("position", position)
//            .add("rarity", rarity.rarName)
//            .add("opened", opened)
//            .build()
//    }

}
