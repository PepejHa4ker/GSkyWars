package com.pepej.gskywars

import com.pepej.gskywars.commands.GSkyWarsCommands
import com.pepej.gskywars.config.Config
import com.pepej.gskywars.database.DatabaseAdapter
import com.pepej.gskywars.events.GSkyWarsEventListener
import com.pepej.gskywars.game.Game
import com.pepej.gskywars.managers.IslandManager
import com.pepej.gskywars.managers.NpcManager
import com.pepej.gskywars.managers.UserManager
import com.pepej.gskywars.model.*
import com.pepej.papi.ap.Plugin
import com.pepej.papi.ap.PluginDependency
import com.pepej.papi.maven.MavenLibraries
import com.pepej.papi.maven.MavenLibrary
import com.pepej.papi.plugin.PapiJavaPlugin
import java.io.File


@Plugin(
    name = "GSkyWars",
    version = "1.0.0-gamma",
    description = "Skywars plugin",
    depends = [PluginDependency("papi")]
)
@MavenLibraries(
    MavenLibrary("com.zaxxer:HikariCP:3.4.5"),
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
    private val resources = listOf("config", "assets/heads", "assets/trails").map { it.plus(".json") }


    override fun onPluginEnable() {
        instance = this
        resources.forEach { saveResource(it, false) }
        Head.load(File(dataFolder, "assets/heads.json"))
        Trail.load(File(dataFolder, "assets/trails.json"))
        config = Config(File(dataFolder, "config.json"))
        databaseAdapter = DatabaseAdapter()
        databaseAdapter.open()
        bind(databaseAdapter)
        game = Game()
        NpcManager()
        IslandManager()
        userManager = UserManager()
        bindModule(GSkyWarsEventListener())
        bindModule(GSkyWarsCommands())
        bindModule(userManager)
        bindModule(game)
        bindModule(GiantKit())
        bindModule(TrollKit())


    }

    override fun onPluginDisable() {
        UserManager.users.forEach(User::save)
    }

    companion object {
        lateinit var instance: GSkyWars
            private set

    }
}