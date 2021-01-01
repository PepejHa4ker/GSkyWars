package com.pepej.gskywars.database

import com.pepej.gskywars.database.user.UserAdapter


class DatabaseAdapter : AutoCloseable {
    lateinit var userAdapter: UserAdapter
    private var databaseManager: DatabaseManager? = null

    init {
        setup()
    }

    fun open() {
        if (databaseManager != null) {
            databaseManager = DatabaseManager()
        }

    }

    private fun setup() {
        this.userAdapter = UserAdapter()
    }

    override fun close() {
        databaseManager?.hikari?.close()
        databaseManager?.hikari?.connection?.close()

    }

}