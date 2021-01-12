package com.pepej.gskywars.generic

import com.pepej.gskywars.model.Head
import com.pepej.papi.item.ItemStackBuilder
import com.pepej.papi.menu.Item
import com.pepej.papi.metadata.Metadata
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object GenericItems {

    val ISLAND_SELECTOR_ITEM: ItemStack = ItemStackBuilder.head(Head.findByName("Grass Orb").texture)
        .nameClickable("&aВыбор острова")
        .loreClickable("открыть меню выбора островов")
        .build()


    val SPECTATOR_ITEM: ItemStack = ItemStackBuilder.head(Head.findByName("Ricardo Milos").texture)
        .nameClickable("&7Меню наблюдателя")
        .loreClickable("открыть меню наблюдателя")
        .build()
    val VOTE_MENU: Item = ItemStackBuilder.head(Head.findByName("Like").texture)
        .name("&bМеню репутаций")
        .lore("  &eЧто это такое?")
        .lore("    &7- Это система репутаций с помощью которой", " &7Вы можете повышать/понижать чужую репутацию")
        .lore("  &eКак это сделать?")
        .lore("    &7- Наведите на голову игрока и нажмите нужную кнопку")
        .buildItem()
        .build()

    val GOTO_PREVIOUS: Item = ItemStackBuilder.head(Head.findByName("Red Arrow Left").texture)
        .nameClickable("&cНазад")
        .buildConsumer {
            val player = it.whoClicked as Player
            val meta = Metadata.provideForPlayer(player)
            val lastMenu = meta.getOrNull(GenericMetadata.PREVIOUS_MENU)
            if (lastMenu != null) {
                lastMenu.open()
            } else {
                player.closeInventory()
            }
        }

    val PROFILE_MENU = ItemStackBuilder.of(Material.NETHER_STAR).nameClickable("&7Профиль").build()

}