package com.pepej.gskywars.events

import com.pepej.gskywars.generic.GenericItems
import com.pepej.gskywars.menu.IslandSelectorMenu
import com.pepej.gskywars.menu.ProfileMenu
import com.pepej.gskywars.menu.SpectatorMenu
import com.pepej.gskywars.utils.SquarelandApi
import com.pepej.papi.events.Events.subscribe
import com.pepej.papi.item.ItemStackBuilder
import com.pepej.papi.terminable.TerminableConsumer
import com.pepej.papi.terminable.module.TerminableModule
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent


class GSkyWarsEventListener : TerminableModule {


//    companion object {
//        val canSeeGlow = mutableListOf<Player>()
//    }

    override fun setup(consumer: TerminableConsumer) {
        subscribe(PlayerJoinEvent::class.java)
            .filter { it.player.inventory.getItem(0) != GenericItems.ISLAND_SELECTOR_ITEM }
            .handler {
                it.player.inventory.setItem(0, GenericItems.ISLAND_SELECTOR_ITEM)
            }
            .bindWith(consumer)

        subscribe(PlayerJoinEvent::class.java)
            .filter { it.player.inventory.getItem(4) != GenericItems.SPECTATOR_ITEM }
            .handler {
                it.player.inventory.setItem(4, GenericItems.SPECTATOR_ITEM)
            }
            .bindWith(consumer)

        subscribe(PlayerInteractEvent::class.java)
            .filter { it.player.inventory.itemInMainHand == GenericItems.ISLAND_SELECTOR_ITEM }
            .handler {
                IslandSelectorMenu(it.player).open()
                it.isCancelled = true
            }
            .bindWith(consumer)
        subscribe(PlayerInteractEvent::class.java)
            .filter { it.player.inventory.itemInMainHand == GenericItems.profileMenu(it.player.name) }
            .handler {
                ProfileMenu(it.player).open()
                it.isCancelled = true
            }
            .bindWith(consumer)

        subscribe(PlayerInteractEvent::class.java)
            .filter { it.player.inventory.itemInMainHand == GenericItems.SPECTATOR_ITEM }
            .handler {
                SpectatorMenu(it.player).open()
                it.isCancelled = true
            }
            .bindWith(consumer)
        subscribe(PlayerDropItemEvent::class.java)
            .filter { it.itemDrop == GenericItems.ISLAND_SELECTOR_ITEM }
            .handler { it.isCancelled = true }
            .bindWith(consumer)
        subscribe(PlayerDropItemEvent::class.java)
            .filter { it.itemDrop == GenericItems.SPECTATOR_ITEM }
            .handler { it.isCancelled = true }
            .bindWith(consumer)
        subscribe(PlayerDropItemEvent::class.java)
            .filter { it.itemDrop == GenericItems.VOTE_MENU }
            .handler { it.isCancelled = true }
            .bindWith(consumer)
        subscribe(PlayerJoinEvent::class.java)
            .handler {
                it.player.inventory.setItem(
                    1,
                    ItemStackBuilder.of(SquarelandApi.getSkull(it.player.name)).nameClickable("&7Профиль").build()
                )
            }
            .bindWith(consumer)

        subscribe(InventoryClickEvent::class.java)
            .filter { it.currentItem == GenericItems.ISLAND_SELECTOR_ITEM }
            .handler {
                it.isCancelled = true
                IslandSelectorMenu(it.whoClicked as Player).open()
            }
            .bindWith(consumer)
        subscribe(InventoryClickEvent::class.java)
            .filter { it.clickedInventory == null }
            .handler { it.whoClicked.closeInventory() }
            .bindWith(consumer)

    }
}
