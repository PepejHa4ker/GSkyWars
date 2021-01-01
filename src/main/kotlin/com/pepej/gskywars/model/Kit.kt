package com.pepej.gskywars.model

import com.google.gson.JsonElement
import com.pepej.papi.gson.GsonSerializable
import com.pepej.papi.gson.JsonBuilder
import com.pepej.papi.terminable.module.TerminableModule
import org.bukkit.inventory.ItemStack
import java.util.*

abstract class Kit(
    val id: Int,
    val name: String,
    val price: Int,
    val default: Boolean = false,
    val menuItem: ItemStack
) : Comparable<Kit>, TerminableModule, GsonSerializable {

    companion object {
        val kits: MutableSet<Kit> = TreeSet()
    }

    fun has(user: User): Boolean {
        return default || user.kits.contains(this)
    }

    override fun serialize(): JsonElement {
        return JsonBuilder.`object`()
            .add("id", id)
            .add("name", name)
            .add("price", price)
            .build()

    }

    override fun compareTo(other: Kit): Int {
        return price.compareTo(other.price)
    }

    abstract fun equip(user: User)



}
