package com.pepej.gskywars.managers


import com.google.gson.JsonParser
import com.pepej.gskywars.GSkyWars.Companion.instance
import com.pepej.gskywars.menu.IslandSelectorMenu
import com.pepej.papi.Services
import com.pepej.papi.npc.CitizensNpcFactory
import com.pepej.papi.serialize.Position

class NpcManager {

    init {
        val npcFactory = Services.load(CitizensNpcFactory::class.java)
        val config = instance.config.rootConfigNode.getNode("npc")
        val parser = JsonParser()
        val json = parser.parse(config.getNode("position").string)
        val position = Position.deserialize(json)
        val npc = npcFactory.spawnNpc(
            position.toLocation(),
            config.getNode("name").string,
            config.getNode("skin").getNode("data").string,
            config.getNode("skin").getNode("signature").string
        )

        npc.setClickCallback { IslandSelectorMenu(it).open() }
    }
}