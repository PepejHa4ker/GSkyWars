package com.pepej.gskywars.menu

import com.pepej.gskywars.generic.GenericItems
import com.pepej.gskywars.generic.GenericMetadata
import com.pepej.papi.menu.Gui
import com.pepej.papi.metadata.Metadata
import org.bukkit.entity.Player

abstract class InnerGui(player: Player, lines: Int, title: String) : Gui(player, lines, title) {

    abstract val previous: Gui

    override fun open() {
        val meta = Metadata.provideForPlayer(player)
        meta.forcePut(GenericMetadata.PREVIOUS_MENU, previous)
        super.open()
    }

    override fun redraw() {
        setItem(0, GenericItems.GOTO_PREVIOUS)
    }
}