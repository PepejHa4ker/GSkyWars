package com.pepej.gskywars.menu

import com.pepej.gskywars.utils.SquarelandApi
import com.pepej.gskywars.utils.asUser
import com.pepej.papi.item.ItemStackBuilder
import com.pepej.papi.menu.Gui
import com.pepej.papi.menu.scheme.StandardSchemeMappings
import org.bukkit.entity.Player

class ProfileMenu(player: Player): Gui(player, 2, "Профиль ${player.name}") {
    override fun redraw() {
        fillNullableWith(StandardSchemeMappings.STAINED_GLASS.get(7).get())
        addItem(
            ItemStackBuilder.of(SquarelandApi.getSkull(player.name))
                .name("&bВаша статистика ->")
                .lore("    &aУбийств: ${player.asUser().kills}")
                .buildItem()
                .build()
        )
    }
}