package com.pepej.gskywars.model

import com.google.common.base.Preconditions
import com.google.gson.JsonElement
import com.pepej.gskywars.GSkyWars
import com.pepej.gskywars.GSkyWars.Companion.instance
import com.pepej.gskywars.managers.UserManager.Companion.users
import com.pepej.gskywars.utils.toStr
import com.pepej.papi.gson.GsonProvider
import com.pepej.papi.gson.GsonSerializable
import com.pepej.papi.gson.JsonBuilder
import com.pepej.papi.utils.UndashedUuids
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

data class User(
    val id: UUID,
    val username: String,
    var kits: MutableList<Kit?> = mutableListOf(),
    var reputation: Int = 0,
    var lastVoteTimeStamp: Long = 0,
    var activeKit: Int = 0,
    var games: Int = 0,
    var wins: Int = 0,
    var kills: Int = 0,
    var deaths: Int = 0,
    var arrowsFired: Int = 0,
    var blocksPlaced: Int = 0,
    var blocksBroken: Int = 0,
    var localKills: Int = 0,
    var island: Island? = null,
    var spectator: Boolean = false,
) : GsonSerializable {

    companion object {
        fun deserialize(element: JsonElement): User? {
            Preconditions.checkArgument(element.isJsonObject)
            val jObject = element.asJsonObject
            Preconditions.checkArgument(jObject.has("id"))
            Preconditions.checkArgument(jObject.has("username"))
            return GsonProvider.standard().fromJson(element, User::class.java)
        }
    }

    override fun serialize(): JsonElement {
        return JsonBuilder.`object`()
            .add("id", id.toStr())
            .add("username", username)
            .add("kits", JsonBuilder.array().addSerializables(kits).build())
            .add("last_vote_time_stamp", lastVoteTimeStamp)
            .add("reputation", reputation)
            .add("active_kit", activeKit)
            .add("games", games)
            .add("wins", wins)
            .add("kills", kills)
            .add("deaths", deaths)
            .add("arrows_fired", arrowsFired)
            .add("blocks_placed", blocksPlaced)
            .add("blocks_broken", blocksBroken)
            .build()
    }

    val toPlayer: Player
        get() = Bukkit.getPlayer(id)

    val canVote: Boolean
        get() = ((System.currentTimeMillis() - lastVoteTimeStamp) / 1000) > instance.config.voteDelay.seconds

    val voteDelay: Long
        get() = ((lastVoteTimeStamp - System.currentTimeMillis()) / 1000) + instance.config.voteDelay.seconds


    operator fun unaryMinus() {
        for (kit in kits) {
            GSkyWars.instance.databaseAdapter.userAdapter.updateKits(id.toStr(), kit?.id ?: continue)
        }
        GSkyWars.instance.databaseAdapter.userAdapter.updateUser(UndashedUuids.toString(id), this)
        Island.islands.mapNotNull { it.users }.filter { this in it }.forEach { it.remove(this) }
        users.remove(this)

    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (id != other.id) return false
        if (username != other.username) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + username.hashCode()
        return result
    }
}