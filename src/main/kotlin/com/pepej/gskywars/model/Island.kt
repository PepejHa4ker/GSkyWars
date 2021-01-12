package com.pepej.gskywars.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Island(
    val id: Int,
    val chests: MutableList<Chest>,
    val spawns: MutableList<Spawn>,
    var users: MutableList<User> = mutableListOf(),
    @Transient var slot: Int = 0,
    @Transient var tag: String = "A"
) {
    companion object {
        val islands: MutableList<Island> = mutableListOf()

    }

}
