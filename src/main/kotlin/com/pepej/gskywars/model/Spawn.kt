package com.pepej.gskywars.model

import kotlinx.serialization.Serializable

@Serializable
data class Spawn(val id: Int, val position: Position)
