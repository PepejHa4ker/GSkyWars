package com.pepej.gskywars.menu

import com.pepej.gskywars.GSkyWars.Companion.instance
import com.pepej.gskywars.model.Head
import com.pepej.gskywars.model.Island
import com.pepej.gskywars.utils.asUser
import com.pepej.papi.item.ItemStackBuilder
import com.pepej.papi.menu.Gui
import com.pepej.papi.menu.scheme.MenuScheme
import com.pepej.papi.menu.scheme.StandardSchemeMappings
import org.bukkit.entity.Player

class IslandSelectorMenu(player: Player) : Gui(player, 3, "&6Выберите остров") {

    companion object {

        private val ISLANDS_SCHEME: MenuScheme = MenuScheme()
            .mask("001000100")
            .maskEmpty(1)
            .mask("001000100")

    }

    override fun redraw() {
        fillNullableWith(StandardSchemeMappings.STAINED_GLASS.get(7).get())
        val user = player.asUser()
        val populator = ISLANDS_SCHEME.newPopulator(this)
        setItem(13, ItemStackBuilder.head(Head.findByName("Clock").texture)
            .nameClickable("&bСлучайный остров")
            .buildConsumer {
                instance.game.joinRandomIsland((it.whoClicked as Player).asUser())
                redraw()
            }
        )

        Island.islands.forEach {
            val builder = ItemStackBuilder.of(StandardSchemeMappings.HARDENED_CLAY.get(13).get().itemStack)
                .name("&6Остров &c#${it.id}")
                .loreUnique("&a${it.users.size}&6/&a${it.spawns.size}")
            it.users.forEach { u ->
                builder.loreUnique("&7${u.username}")
            }

            populator.accept(builder.data(if (it.users.size >= it.spawns.size) 14 else if (user.island == it) 4 else 13)
                .buildConsumer { _ ->
                    if (user !in it.users) {
                        instance.game.join(user, it)
                        redraw()
                    }
                })
        }

    }

}