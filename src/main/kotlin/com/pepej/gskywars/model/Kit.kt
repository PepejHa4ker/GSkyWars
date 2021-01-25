package com.pepej.gskywars.model

import com.pepej.papi.item.ItemStackBuilder
import com.pepej.papi.terminable.TerminableConsumer
import com.pepej.papi.terminable.module.TerminableModule
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import java.util.*

sealed class Kit(
    val id: Int,
    val name: String,
    val price: Int,
    val default: Boolean = false,
    val menuItem: ItemStack
) : Comparable<Kit>, TerminableModule {

    companion object {
        val kits: MutableSet<Kit> = TreeSet()
    }

    fun has(user: User): Boolean {
        return default || user.kits.contains(this)
    }

    override fun compareTo(other: Kit): Int {
        return price.compareTo(other.price)
    }

    abstract fun equip(user: User)

}

class TrollKit : Kit(
    2,
    "Тролль",
    250,
    false,
    ItemStackBuilder.of(Material.FIREWORK)
        .name("&6Хлопушка тролля")
        .lore("&7Карманная хлопушка тролля для издевательства над игроками")
        .build()

) {
    override fun equip(user: User) {
        user.toPlayer.inventory.apply {
            helmet = ItemStackBuilder.of(Material.LEATHER_HELMET).name("&dШапка Тролля").build()
            itemInMainHand = ItemStackBuilder.of(Material.STICK).name("&dВолшебная палочка")
                .enchant(Enchantment.DAMAGE_ALL, 5)
                .enchant(Enchantment.KNOCKBACK, 2)
                .build()

        }

    }

    override fun setup(consumer: TerminableConsumer) {
    }

    init {
        kits.add(this)
    }
}

class GiantKit : Kit(
    1,
    "Гигант",
    100,
    true,
    ItemStackBuilder.of(Material.LEATHER_CHESTPLATE)
        .name("&dКуртка циклопа")
        .lore("&7Когда-то её носил сам циклоп, которого убил Одиссей")
        .build()) {

    override fun equip(user: User) {
        //
    }

    override fun setup(consumer: TerminableConsumer) {
    }

    init {
        kits.add(this)
    }
}
