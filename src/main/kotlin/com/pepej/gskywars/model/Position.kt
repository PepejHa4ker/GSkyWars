package com.pepej.gskywars.model

import com.pepej.papi.Papi
import kotlinx.serialization.Serializable
import org.bukkit.Location

@Serializable
data class Position (
    var x: Double,
    var y: Double,
    var z: Double,
    val world: String
) {

    companion object {
        fun of(location: Location): Position {
            return Position(location.x, location.y, location.z, location.world.name)
        }
    }

    fun toLocation(): Location {
        return Location(Papi.worldNullable(world), x,y,z)
    }

    fun add(x: Double = 0.0, y: Double = 0.0, z: Double = 0.0): Position {
        this.x += x
        this.y += y
        this.z += z
        return this

    }

    fun add(x: Int = 0, y: Int = 0, z: Int = 0) : Position {
        return add(x.toDouble(), y.toDouble(), z.toDouble())
    }


    fun add(a: Double): Position  {
        return add(a, a, a)
    }
}
