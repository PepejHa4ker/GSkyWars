package com.pepej.gskywars.managers

import com.pepej.gskywars.GSkyWars.Companion.instance
import com.pepej.gskywars.model.Island
import com.pepej.papi.Services
import com.pepej.papi.gson.GsonProvider
import com.pepej.papi.hologram.HologramFactory
import com.pepej.papi.math.vector.Vector3d
import com.pepej.papi.terminable.TerminableConsumer
import com.pepej.papi.terminable.module.TerminableModule


class IslandManager : TerminableModule {
    override fun setup(consumer: TerminableConsumer) {
        val hologramFactory = Services.load(HologramFactory::class.java)
        for (islandNode in instance.config.rootConfigNode.getNode("islands").childrenList) {
            val island = Island.deserialize(GsonProvider.parser().parse(islandNode.string)) ?: continue
            Island.islands.add(island)
            for (chest in island.chests) {
                val hologram = hologramFactory.newHologram(chest.position.add(Vector3d(0.0, 0.2, 0.0)), "&7Сундук с лутом" )
                hologram.spawn()

            }
        }
    }
}