package com.pepej.gskywars

import com.pepej.gskywars.commands.GSkyWarsCommands
import com.pepej.gskywars.config.Config
import com.pepej.gskywars.database.DatabaseAdapter
import com.pepej.gskywars.events.GSkyWarsEventListener
import com.pepej.gskywars.game.Game
import com.pepej.gskywars.kit.GiantKit
import com.pepej.gskywars.kit.TrollKit
import com.pepej.gskywars.managers.IslandManager
import com.pepej.gskywars.managers.NpcManager
import com.pepej.gskywars.managers.UserManager
import com.pepej.gskywars.model.Head
import com.pepej.gskywars.model.User
import com.pepej.papi.ap.Plugin
import com.pepej.papi.ap.PluginDependency
import com.pepej.papi.plugin.PapiJavaPlugin
import java.io.File

@Plugin(
    name = "GSkyWars",
    version = "1.0.0-gamma",
    description = "Skywars plugin",
    depends = [PluginDependency("papi")]
)

class GSkyWars : PapiJavaPlugin() {
    lateinit var userManager: UserManager
        private set
    lateinit var databaseAdapter: DatabaseAdapter
        private set
    lateinit var game: Game
        private set
    lateinit var config: Config
        private set
    private val resources = listOf("config", "assets/heads").map { it.plus(".json") }


    override fun onPluginLoad() {
        instance = this
        for (resource in resources) {
            saveResource(resource, false)
        }
        Head.load(File(dataFolder, "assets/heads.json"))
        config = Config(File(dataFolder, "config.json"))


    }

    override fun onPluginEnable() {
        databaseAdapter = DatabaseAdapter()
        databaseAdapter.open()
        bind(databaseAdapter)
        game = Game()
        NpcManager()
        userManager = UserManager()
        bindModule(GSkyWarsEventListener())
        bindModule(GSkyWarsCommands())
        bindModule(IslandManager())
        bindModule(userManager)
        bindModule(GiantKit())
        bindModule(TrollKit())

    }

    override fun onPluginDisable() {
        UserManager.users.forEach(User::unaryMinus)
    }

    companion object {
        lateinit var instance: GSkyWars
            private set

    }
}