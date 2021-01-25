package com.pepej.gskywars.menu

import com.pepej.gskywars.generic.GenericItems
import com.pepej.gskywars.generic.GenericMetadata
import com.pepej.papi.menu.Menu
import com.pepej.papi.metadata.Metadata
import org.bukkit.entity.Player

abstract class InnerMenu(player: Player, lines: Int, title: String) : Menu(player, lines, title) {

    abstract val previous: Menu

    override fun open() {
        val meta = Metadata.provideForPlayer(player)
        meta.forcePut(GenericMetadata.PREVIOUS_MENU, previous)
        super.open()
    }

    override fun redraw() {
        setItem(0, GenericItems.GOTO_PREVIOUS)
    }
}