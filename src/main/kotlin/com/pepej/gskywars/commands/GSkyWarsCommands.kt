package com.pepej.gskywars.commands

import com.pepej.gskywars.GSkyWars.Companion.instance
import com.pepej.gskywars.managers.UserManager
import com.pepej.gskywars.menu.KitSelectorMenu
import com.pepej.gskywars.utils.msg
import com.pepej.papi.command.Commands
import com.pepej.papi.terminable.TerminableConsumer
import com.pepej.papi.terminable.module.TerminableModule
import com.pepej.papi.utils.Players
import java.util.*

class GSkyWarsCommands : TerminableModule {
    override fun setup(consumer: TerminableConsumer) {
        Commands.create()
            .assertUsage("<username>")
            .handler {
                val player = it.arg(0).parseOrFail(String::class.java)
                val json = UserManager.users.find { u -> u.username.equals(player, true) } ?: return@handler
                Players.all().forEach { p ->
                    p.msg(json.serialize().toString())
                }
            }
            .registerAndBind(consumer, "userinfo")

        Commands.create()
            .assertPlayer()
            .handler {
                KitSelectorMenu(it.sender()).open()
            }
            .registerAndBind(consumer, "kit")

    }
}