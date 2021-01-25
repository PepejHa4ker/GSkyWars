package com.pepej.gskywars.model

import com.pepej.papi.hologram.individual.IndividualHologram
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Chest(
    val id: Int,
    val position: Position,
    val rarity: Rarity,
    @Transient var opened: Boolean = false,
    @Transient var updateHologram: IndividualHologram? = null,
) {





    enum class Rarity(val rarName: String) {
        COMMON("Обычный"),
        MIDDLE("Редкий"),
        MYTHIC("Мистический")

    }

}
