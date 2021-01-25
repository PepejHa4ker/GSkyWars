package com.pepej.gskywars.database.user

import com.pepej.gskywars.database.DatabaseManager
import com.pepej.gskywars.model.Kit
import com.pepej.gskywars.model.Trail
import com.pepej.gskywars.model.User
import com.pepej.gskywars.utils.toStr

class UserAdapter {

    private val userDao: UserDao = DatabaseManager().jdbi.onDemand(UserMySQLProvider::class.java)

    fun userExists(id: String): Boolean {
       return userDao.userExists(id)
    }

    fun getAllUsers(): List<User?> {
        return userDao.getAllUsers()
    }

    fun getAllUserIds(): List<String?> {
        return userDao.getAllUserIds()
    }

    fun getUser(id: String): User? {
        return userDao.getUser(id)
    }

    fun getUserByName(name: String): User? {
        return userDao.getUserByName(name)
    }

    fun getKits(id: String): MutableList<Kit?> {
        return userDao.getKits(id)
    }

    fun getTrails(id: String): MutableList<Trail?> {
        return userDao.getTrails(id)
    }

    fun updateTrail(id: String, trail: Trail) {
        userDao.updateTrail(id, trail)
    }


    fun updateKit(id: String, kitId: Int) {
        return userDao.updateKit(id, kitId)
    }

    fun createUser(id: String, username: String) {
       userDao.createUser(id, username)
    }

    fun updateUser(user: User) {
        val (id, _, repo, vote, kit, _, games, wins, kills, deaths, arrows, placed, broken) = user
        userDao.updateUser(id.toStr(), kit, repo, vote, games, wins, kills, deaths, arrows, placed, broken)
    }

    fun deleteUser(id: String) {
        userDao.deleteUser(id)
    }
}