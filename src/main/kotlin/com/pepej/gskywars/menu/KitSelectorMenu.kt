package com.pepej.gskywars.menu

import com.pepej.gskywars.model.Kit
import com.pepej.gskywars.utils.asUser
import com.pepej.gskywars.utils.msg
import com.pepej.papi.item.ItemStackBuilder
import com.pepej.papi.menu.Gui
import com.pepej.papi.menu.scheme.MenuScheme
import com.pepej.papi.menu.scheme.StandardSchemeMappings
import com.pepej.papi.utils.Players
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player

class KitSelectorMenu(player: Player) : Gui(player, 6, "Выбор набора") {
    companion object {

        private val BORDERS_SCHEME: MenuScheme = MenuScheme(StandardSchemeMappings.STAINED_GLASS)
            .mask("111111111")
            .mask("110000011")
            .mask("110000011")
            .mask("110000011")
            .mask("110000011")
            .mask("111111111")
            .scheme(7, 7, 7, 7, 7, 7, 7, 7, 7)
            .scheme(7, 7, 7, 7)
            .scheme(7, 7, 7, 7)
            .scheme(7, 7, 7, 7)
            .scheme(7, 7, 7, 7)
            .scheme(7, 7, 7, 7, 7, 7, 7, 7, 7)

        private val ITEMS_SCHEME: MenuScheme = MenuScheme()
            .maskEmpty(1)
            .mask("001111100")
            .mask("001111100")
            .mask("001111100")
            .mask("001111100")
            .maskEmpty(1)

    }

    override fun redraw() {
            BORDERS_SCHEME.apply(this)
            val itemPopulator = ITEMS_SCHEME.newPopulator(this)
            val user = player.asUser()
            Kit.kits.forEach {
                if (it.has(user)) {
                    val builder = ItemStackBuilder.of(it.menuItem)
                    if (user.activeKit == it.id) {
                        builder.loreUnique("&aТекущий")
                        builder.enchant(Enchantment.ARROW_DAMAGE)
                        builder.hideAttributes()
                    }

                    val item = builder.buildConsumer { _ ->
                        if (user.activeKit != it.id) {
                            user.activeKit = it.id
                            user.toPlayer.msg(Players.MessageType.ANNOUNCEMENT, "Вы успешно выбрали набор &6${it.name}")
                            redraw()

                        }
                    }

                    itemPopulator.accept(item)

                } else {
                    val builder = ItemStackBuilder.of(StandardSchemeMappings.STAINED_GLASS.get(14).get().itemStack)
                        .name("&cЗаблокирован")
                        .loreClickable("купить набор ${it.name} за &a${it.price}&7 ионет")
                        .buildConsumer { _ ->
                            user.kits.add(it)
                            user.toPlayer.msg(Players.MessageType.ANNOUNCEMENT, "Вы успешно приобрели набор &6${it.name}")
                            redraw()

                        }
                    itemPopulator.accept(builder)

                }
            }

    }
}