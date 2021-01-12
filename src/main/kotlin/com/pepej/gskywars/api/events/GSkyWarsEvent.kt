package com.pepej.gskywars.api.events

import com.pepej.gskywars.game.Game
import com.pepej.gskywars.model.User
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

sealed class GSkyWarsEvent : Event() {
    override fun getHandlers(): HandlerList {
        return HANDLERS
    }

    companion object {
        private val HANDLERS = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return HANDLERS
        }
    }
}

class GSkyWarsGameStartEvent(val game: Game): GSkyWarsEvent(), Cancellable {

    private var cancelled: Boolean = false

    override fun isCancelled(): Boolean {
        return cancelled
    }

    override fun setCancelled(cancel: Boolean) {
        cancelled = cancel
    }

}
class GSkyWarsUserJoinEvent(val game: Game, val user: User): GSkyWarsEvent(), Cancellable {
    private var cancelled: Boolean = false

    override fun isCancelled(): Boolean {
        return cancelled
    }

    override fun setCancelled(cancel: Boolean) {
        cancelled = cancel
    }

}

class GSkyWarsGameEndEvent(val game: Game): GSkyWarsEvent()
class GSkyWarsUserLeaveEvent(val game: Game, val user: User): GSkyWarsEvent()
