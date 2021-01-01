package com.pepej.gskywars.model

import com.google.common.base.Preconditions
import com.google.gson.JsonElement
import com.pepej.papi.gson.GsonProvider
import com.pepej.papi.gson.GsonSerializable
import com.pepej.papi.gson.JsonBuilder
import com.pepej.papi.terminable.TerminableConsumer
import com.pepej.papi.terminable.composite.CompositeTerminable
import com.pepej.papi.terminable.module.TerminableModule

data class Island(
    val id: Int,
    val chests: MutableList<Chest>,
    val spawns: MutableList<Spawn>,
    var users: MutableList<User>? = mutableListOf(),
    val slot: Int = 0,
    var tag: String = "A"


): TerminableConsumer, GsonSerializable {
    companion object {
        val islands: MutableList<Island> = mutableListOf()

        fun deserialize(element: JsonElement): Island? {
            Preconditions.checkArgument(element.isJsonObject)
            val jObject = element.asJsonObject
            Preconditions.checkArgument(jObject.has("id"))
            Preconditions.checkArgument(jObject.has("spawns"))
            Preconditions.checkArgument(jObject.has("chests"))
            return GsonProvider.standard().fromJson(element, Island::class.java)

        }
    }

    override fun serialize(): JsonElement {
        return JsonBuilder.`object`()
            .add("id", id)
            .add("spawns", JsonBuilder.array().addSerializables(spawns).build())
            .add("chests", JsonBuilder.array().addSerializables(chests).build())
            .add("users", JsonBuilder.array().addSerializables(users).build())
            .add("slot", slot)
            .add("tag", tag)
            .build()

    }

    val isFull: Boolean
    get() {
        return users?.size ?: 0 >= spawns.size
    }

    override fun <T : AutoCloseable> bind(terminable: T): T {
        return listener.bind(terminable)
    }

    override fun <T : TerminableModule> bindModule(module: T): T {
        return listener.bindModule(module)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Island

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }

    private val listener = CompositeTerminable.create()


    }
