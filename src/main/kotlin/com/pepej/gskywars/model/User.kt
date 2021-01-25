package com.pepej.gskywars.model

import com.pepej.gskywars.GSkyWars.Companion.instance
import com.pepej.gskywars.managers.UserManager.Companion.users
import com.pepej.gskywars.model.serialization.UUIDSerializer
import com.pepej.gskywars.utils.toStr
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*



@Serializable
data class User(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val username: String,
    var reputation: Int = 0,
    @SerialName("last_vote_timestamp") var lastVoteTimeStamp: Long = 0,
    @SerialName("active_kit") var activeKit: Int = 0,
    @SerialName("active_trail") var activeTrail: Int = 0,
    var games: Int = 0,
    var wins: Int = 0,
    var kills: Int = 0,
    var deaths: Int = 0,
    @SerialName("arrows_fired") var arrowsFired: Int = 0,
    @SerialName("blocks_placed") var blocksPlaced: Int = 0,
    @SerialName("blocks_broken") var blocksBroken: Int = 0,
    @SerialName("local_kills") var localKills: Int = 0,
    @Transient var kits: MutableList<Kit?> = mutableListOf(),
    @Transient var trails: MutableList<Trail?> = mutableListOf(),
    @Transient var island: Island? = null,
    @Transient var deathLocation: Position? = null,
    var spectator: Boolean = false,
) {

    val toPlayer: Player
        get() = Bukkit.getPlayer(id)

    val canVote: Boolean
        get() = ((System.currentTimeMillis() - lastVoteTimeStamp) / 1000) > instance.config.voteDelay.seconds

    val voteDelay: Long
        get() = ((lastVoteTimeStamp - System.currentTimeMillis()) / 1000) + instance.config.voteDelay.seconds



    fun unload() {
        save()
        users.remove(this)

    }

    fun save() {
        for (kit in kits) {
            instance.databaseAdapter.userAdapter.updateKit(id.toStr(), kit?.id ?: continue)
        }
        for (trail in trails) {
            instance.databaseAdapter.userAdapter.updateTrail(id.toStr(), trail ?: continue)
        }

        instance.databaseAdapter.userAdapter.updateUser(this)
        Island.islands.map { it.users }.filter { this in it }.forEach { it.remove(this) }
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