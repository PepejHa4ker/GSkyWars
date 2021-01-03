package com.pepej.gskywars.managers

import com.pepej.gskywars.GSkyWars.Companion.instance
import com.pepej.gskywars.model.Island
import com.pepej.papi.gson.GsonProvider
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json


class IslandManager  {
    init {
        instance.config.rootConfigNode.getNode("islands").childrenList
            .map { it.string }
            .forEach { Island.islands.add(Json.decodeFromString(GsonProvider.parser().parse(it).toString())) }

        }
    }
