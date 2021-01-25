package com.pepej.gskywars.model

import com.pepej.papi.config.ConfigFactory
import com.pepej.papi.google.gson.JsonElement
import com.pepej.papi.gson.GsonSerializable
import com.pepej.papi.gson.JsonBuilder
import com.pepej.papi.item.ItemStackBuilder
import com.pepej.papi.menu.Item
import kotlinx.serialization.Serializable
import org.bukkit.inventory.ItemStack
import java.io.File

@Serializable
data class Head(val name: String, val texture: String) : GsonSerializable {


    companion object {
        private val heads: MutableList<Head> = mutableListOf()

        fun load(file: File) {
            val config = ConfigFactory.gson().load(file)
            for (configNode in config.childrenList) {
                val head = Head(configNode.getNode("name").string, configNode.getNode("texture").string)
                add(head)
            }
        }

        fun add(head: Head) {
            heads.add(head)
        }

        fun findByName(name: String): Head {
            return heads.find { it.name.equals(name, true) } ?: throw IllegalArgumentException("Cannot find head $name")
        }
    }


    val toItemStack: ItemStack
    get() = ItemStackBuilder.head(texture).name("&7$name").build()

    val toItem: Item
    get() = ItemStackBuilder.head(texture).name("&7$name").buildItem().build()



    override fun serialize(): JsonElement {
        return JsonBuilder.`object`()
            .add("name", name)
            .add("texture", texture)
            .build()
    }

}

