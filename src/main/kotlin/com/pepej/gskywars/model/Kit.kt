package com.pepej.gskywars.model

import com.pepej.papi.terminable.module.TerminableModule
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bukkit.inventory.ItemStack
import java.util.*

@Serializable
abstract class Kit(
    val id: Int,
    val name: String,
    val price: Int,
    val default: Boolean = false,
    @Contextual val menuItem: ItemStack
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
