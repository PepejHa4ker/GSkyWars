package com.pepej.gskywars.generator

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

interface LootGenerator {

    fun basic(): List<ItemStack>

    fun middle(): List<ItemStack>

    fun mythic(): List<ItemStack>

    fun isDiamond(type: Material): Boolean {
        return type.name.startsWith("DIAMOND_")
    }

    fun isIron(type: Material): Boolean {
        return type.name.startsWith("IRON_")
    }

    fun isLeather(type: Material): Boolean {
        return type.name.startsWith("LEATHER_")
    }

    fun isGolden(type: Material): Boolean {
        return type.name.startsWith("GOLD_") || type.name.startsWith("GOLDEN_")
    }
}