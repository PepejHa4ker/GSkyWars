package com.pepej.gskywars.managers

import com.pepej.gskywars.GSkyWars.Companion.instance
import com.pepej.gskywars.database.user.UserAdapter
import com.pepej.gskywars.model.User
import com.pepej.gskywars.utils.asUser
import com.pepej.gskywars.utils.msg
import com.pepej.gskywars.utils.toStr
import com.pepej.papi.events.Events.merge
import com.pepej.papi.events.Events.subscribe
import com.pepej.papi.terminable.TerminableConsumer
import com.pepej.papi.terminable.module.TerminableModule
import com.pepej.papi.utils.Players
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerEvent
import org.bukkit.event.player.PlayerKickEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent

class UserManager : TerminableModule {

    companion object {
        val users: MutableSet<User> = hashSetOf()
    }

    private val userAdapter: UserAdapter = instance.databaseAdapter.userAdapter

    operator fun get(player: Player): User {
        return users.find { it.id == player.uniqueId } ?: loadUser(player)
    }

    private fun loadStats() {
        Players.all().forEach { loadUser(it) }
    }

    private fun loadUser(player: Player): User {
        if (!userAdapter.userExists(player.uniqueId.toStr())) {
            instance.server.consoleSender.msg("Создаю контейнер для пользователя &c#${player.uniqueId.toStr()}...")
            userAdapter.createUser(player.uniqueId.toStr(), player.name)
        }
        val user = userAdapter.getUser(player.uniqueId.toStr()) ?: User(player.uniqueId, player.name)
        user.kits = userAdapter.getKits(player.uniqueId.toStr())
        users.add(user)
        return user
    }

    override fun setup(consumer: TerminableConsumer) {
        subscribe(PlayerLoginEvent::class.java)
            .handler { loadUser(it.player) }
            .bindWith(consumer)
        merge(
            PlayerEvent::class.java,
            PlayerQuitEvent::class.java,
            PlayerKickEvent::class.java
        )
            .handler {
                -it.player.asUser()

            }
            .bindWith(consumer)
    }

    init {
        loadStats()
    }
}