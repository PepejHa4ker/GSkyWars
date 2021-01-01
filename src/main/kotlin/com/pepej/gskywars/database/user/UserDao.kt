package com.pepej.gskywars.database.user

import com.pepej.gskywars.model.Kit
import com.pepej.gskywars.model.User


interface UserDao {

    fun userExists(id: String): Boolean

    fun getAllUsers(): List<User?>

    fun getAllUserIds(): List<String?>

    fun getUser(id: String): User?

    fun getKits(id: String): MutableList<Kit?>

    fun updateKits(id: String, kitId: Int)

    fun createUser(id: String, username: String)

    fun updateUser(
        id: String,
        activeKit: Int,
        reputation: Int,
        lastVoteTimeStamp: Long,
        games: Int,
        wins: Int,
        kills: Int,
        deaths: Int,
        arrowsFired: Int,
        blocksPlaced: Int,
        blocksBroken: Int
    )
    fun deleteUser(id: String)
}