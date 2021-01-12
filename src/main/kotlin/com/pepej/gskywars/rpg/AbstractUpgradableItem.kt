package com.pepej.gskywars.rpg

import com.pepej.gskywars.rpg.items.ExplosionBow
import com.pepej.papi.item.ItemStackBuilder
import com.pepej.papi.terminable.TerminableConsumer
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.reflect.KClass

abstract class AbstractUpgradableItem(val item: Material, private val rarity: UpgradeRarity, private val type: UpgradeType): UpgradableItem, TerminableConsumer {

    companion object {
        val items = mutableMapOf<Player, List<AbstractUpgradableItem>>()


        fun findForPlayer(player: Player): List<AbstractUpgradableItem> {
            return items[player] ?: throw IllegalArgumentException()
        }

         fun <T : AbstractUpgradableItem> create(player: Player, t: T) {
            val oldList = items[player] ?: return
            oldList.toMutableList().add(t)
            items[player] = oldList
        }

    }

    abstract fun registerListener()

    override fun getItemStack(): ItemStack {
        return ItemStackBuilder.of(item).build()
    }

    override fun getRarity(): UpgradeRarity {
        return rarity
    }

    override fun getType(): UpgradeType {
        return type
    }


}