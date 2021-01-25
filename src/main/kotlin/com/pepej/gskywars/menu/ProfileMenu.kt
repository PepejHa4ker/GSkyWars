package com.pepej.gskywars.menu

import com.pepej.gskywars.model.Head
import com.pepej.gskywars.model.Trail
import com.pepej.gskywars.utils.SquarelandApi
import com.pepej.gskywars.utils.asUser
import com.pepej.gskywars.utils.msg
import com.pepej.gskywars.utils.round
import com.pepej.papi.item.ItemStackBuilder
import com.pepej.papi.menu.Menu
import com.pepej.papi.menu.scheme.MenuScheme
import com.pepej.papi.menu.scheme.StandardSchemeMappings
import com.pepej.papi.utils.Players
import org.bukkit.Material
import org.bukkit.entity.Player

class ProfileMenu(player: Player) : Menu(player, 2, "Профиль ${player.name}") {
    override fun redraw() {
        val user = player.asUser()
        val (_, _, rep, _, _, _, games, wins, kills, deaths, arrows, placed, broken) = user
        addItem(
            ItemStackBuilder.of(SquarelandApi.getSkull(player.name))
                .name("&bВаша статистика ->")
                .lore("    &7Репутация &d$rep")
                .lore("    &7Убийств &d$kills")
                .lore("    &7Смертей &d$deaths")
                .lore("    &7Игр сыграно &d$games")
                .lore("    &7Побед &d$wins")
                .lore("    &7Процент побед &d${(games/wins).toDouble().round(2)}%")
                .lore("    &7Выстрелов с лука &d$arrows")
                .lore("    &7Блоков поставлено &d$placed")
                .lore("    &7Блоков разрушено &d$broken")
                .buildItem()
                .build()
        )

        addItem(ItemStackBuilder.head(Head.findByName("Rainbow Arrow Up").texture)
            .nameClickable("&eМеню следов")
            .buildConsumer { TrailMenu().open() }
        )

    }


    private inner class TrailMenu(override val previous: Menu = ProfileMenu(player)) : InnerMenu(player, 2, "Выбор следа") {


        private val TRAIL_SCHEME: MenuScheme = MenuScheme()
            .mask("011111111")
            .mask("111111111")

        override fun redraw() {
            super.redraw()
            val trailPopulator = TRAIL_SCHEME.newPopulator(this)
            val user = player.asUser()
            for (trail in Trail.all()) {
                val builder = ItemStackBuilder.of(Material.ARROW)
                    .nameClickable("&b${trail.name}")
                    .lore(trail.description.split(", "))
                    .lore(if (trail in user.trails) "&8(Куплен)" else "&eКликните, чтобы приобрести")
                if (user.activeTrail == trail.id) {
                    builder.lore("&a(Текущий)")
                }

                val item = builder.buildConsumer {
                    if (trail in user.trails) {
                        if (user.activeTrail == trail.id) {
                            return@buildConsumer
                        } else {
                            user.activeTrail = trail.id
                            user.toPlayer.msg(Players.MessageType.ANNOUNCEMENT, "Вы успешно выбрали след ${trail.name}")
                            this.redraw()

                        }
                    } else {
                        user.trails.add(trail)
                        user.toPlayer.msg(Players.MessageType.ANNOUNCEMENT, "Вы успешно приобрели след ${trail.name}")
                        user.activeTrail = trail.id
                        this.redraw()

                    }
                }
                trailPopulator.accept(item)
            }
            fillNullableWith(StandardSchemeMappings.STAINED_GLASS.get(7).get())
        }
    }
}