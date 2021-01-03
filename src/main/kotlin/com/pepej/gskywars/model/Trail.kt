package com.pepej.gskywars.model

import com.pepej.papi.config.ConfigFactory
import kotlinx.serialization.Serializable
import org.bukkit.Particle
import java.io.File

@Serializable
data class Trail(
    val id: Int,
    val name: String,
    val description: String,
    val particle: Particle,
) {

    companion object {
        private val trails: MutableList<Trail> = mutableListOf()

        fun load(file: File) {
            val config = ConfigFactory.gson().load(file)
            for (configNode in config.childrenList) {
                val trail = Trail(
                    configNode.getNode("id").int,
                    configNode.getNode("name").string,
                    configNode.getNode("description").string,
                    Particle.valueOf(configNode.getNode("particle").string)
                )
                add(trail)
            }
        }

        fun all(): List<Trail> {
            return trails
        }

        fun findByName(name: String): Trail {
            return all().find { it.name.equals(name, true) }
                ?: throw IllegalArgumentException("Cannot find trail with name $name")
        }

        fun add(trail: Trail) {
            trails.add(trail)
        }

    }


}


enum class TrailType {
    FLAME
}
