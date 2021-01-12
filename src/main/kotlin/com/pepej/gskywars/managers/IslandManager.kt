package com.pepej.gskywars.managers

import com.pepej.gskywars.GSkyWars.Companion.instance
import com.pepej.gskywars.model.Island
import com.pepej.gskywars.model.serialization.Serializer
import com.pepej.papi.gson.GsonProvider


class IslandManager  {
    init {
        instance.config.rootConfigNode.getNode("game").getNode("islands").childrenList
            .map { it.string }
            .forEach {
                val island: Island /*эксплиситное выведение типа не хочу пихать в генерик*/= Serializer.deserialize(GsonProvider.parser().parse(it).toString())
                Island.islands.add(island)
            }

        }
    }
