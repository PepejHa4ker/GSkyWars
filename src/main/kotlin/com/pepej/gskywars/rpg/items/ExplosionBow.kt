package com.pepej.gskywars.rpg.items

import com.pepej.gskywars.rpg.AbstractUpgradableItem
import com.pepej.gskywars.rpg.UpgradeRarity
import com.pepej.gskywars.rpg.UpgradeType
import com.pepej.gskywars.utils.msg
import com.pepej.papi.events.Events
import com.pepej.papi.terminable.composite.CompositeTerminable
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent

class ExplosionBow(val player: Player) : AbstractUpgradableItem(Material.BOW, UpgradeRarity.RAR, UpgradeType.BOW_EXPLOSION) {

    private val listenerMap = mutableMapOf<Player, CompositeTerminable>()

    init {
        listenerMap.putIfAbsent(player, CompositeTerminable.create())
        registerListener()
    }

    override fun registerListener() {
        Events.subscribe(PlayerJoinEvent::class.java)
            .handler { e ->
                findForPlayer(e.player).forEach { item ->

                    e.player.inventory.addItem(item.getItemStack())
                }
                onCreate()
            }
            .bindWith(listenerMap[player] ?: return)
//        Events.subscribe(PlayerInteractEvent::class.java)
//            .filter { it.item != null && it.item == find(UpgradeType.BOW_EXPLOSION).getItemStack() }
//            .handler {
//                onUse(it.player)
//            }
//            .bindWith(listenerMap[player] ?: return)
//
//        Events.subscribe(PlayerDropItemEvent::class.java)
//            .filter { it.itemDrop.itemStack == find(UpgradeType.BOW_EXPLOSION).getItemStack() }
//            .handler {
//                onDelete(it.player)
//            }
//            .bindWith(listenerMap[player] ?: return)
    }

    override fun onUpgrade(player: Player) {
        player.msg("Your item has been success upgraded")
    }

    override fun onCreate() {

        Bukkit.broadcastMessage("Created")
    }

    override fun onGet(player: Player) {
        player.msg("You get the explosion bow")
    }

    override fun onUse(player: Player) {
        player.msg("Пиу-Пау")
    }

    override fun onDelete(player: Player) {
        player.msg("Deleted :(")
        listenerMap[player]?.close()
    }

    override fun <T : AutoCloseable> bind(terminable: T): T {
        return listenerMap[player]?.bind(terminable) ?: throw IllegalArgumentException()
    }

}