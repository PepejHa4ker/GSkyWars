package com.pepej.gskywars.database.user

import com.pepej.gskywars.model.Kit
import com.pepej.gskywars.model.Trail
import com.pepej.gskywars.model.User


interface UserDao {

    fun userExists(id: String): Boolean

    fun getAllUsers(): List<User?>

    fun getAllUserIds(): List<String?>

    fun getUser(id: String): User?

    fun getKits(id: String): MutableList<Kit?>

    fun getTrails(id: String): MutableList<Trail?>

    fun updateTrail(id: String, trail: Trail)

    fun updateKit(id: String, kitId: Int)

    fun createUser(id: String, username: String)

    fun updateUser(user: User)

    fun deleteUser(id: String)
}