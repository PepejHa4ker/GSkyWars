package com.pepej.gskywars.database.user

import com.pepej.gskywars.database.DatabaseManager
import com.pepej.gskywars.model.Kit
import com.pepej.gskywars.model.User

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

    fun getKits(id: String): MutableList<Kit?> {
        return userDao.getKits(id)
    }

    fun updateKits(id: String, kitId: Int) {
        return userDao.updateKits(id, kitId)
    }

    fun createUser(id: String, username: String) {
       userDao.createUser(id, username)
    }

    fun updateUser(id: String, new: User) {
        userDao.updateUser(id, new.activeKit, new.reputation, new.lastVoteTimeStamp, new.games, new.wins, new.kills, new.deaths, new.arrowsFired, new.blocksPlaced, new.blocksBroken)
    }

    fun deleteUser(id: String) {
        userDao.deleteUser(id)
    }






}