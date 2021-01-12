package com.pepej.gskywars.rpg

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

interface UpgradableItem {

    fun onUpgrade(player: Player)

    fun onCreate()

    fun onGet(player: Player)

    fun onUse(player: Player)

    fun onDelete(player: Player)

    fun getItemStack(): ItemStack

    fun getRarity(): UpgradeRarity

    fun getType(): UpgradeType
}

//typealias Callback = UpgradableItem.() -> Unit