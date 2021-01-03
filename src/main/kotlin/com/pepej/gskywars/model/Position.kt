package com.pepej.gskywars.model

import com.pepej.papi.Papi
import kotlinx.serialization.Serializable
import org.bukkit.Location

@Serializable
data class Position(
    val x: Double,
    val y: Double,
    val z: Double,
    val world: String
) {
    fun toLocation(): Location {
        return Location(Papi.worldNullable(world), x,y,z)
    }
}
