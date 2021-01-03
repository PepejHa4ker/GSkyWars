package com.pepej.gskywars.game

import com.pepej.gskywars.GSkyWars.Companion.instance
import com.pepej.gskywars.generic.GenericMetadata.SCOREBOARD_KEY
import com.pepej.gskywars.managers.UserManager
import com.pepej.gskywars.model.Island
import com.pepej.gskywars.model.Kit
import com.pepej.gskywars.model.User
import com.pepej.gskywars.utils.TimeUtils
import com.pepej.gskywars.utils.asUser
import com.pepej.gskywars.utils.msg
import com.pepej.papi.Services
import com.pepej.papi.metadata.Metadata.provideForPlayer
import com.pepej.papi.random.RandomSelector
import com.pepej.papi.scheduler.Schedulers
import com.pepej.papi.scheduler.Task
import com.pepej.papi.scoreboard.Scoreboard
import com.pepej.papi.scoreboard.ScoreboardObjective
import com.pepej.papi.scoreboard.ScoreboardProvider
import com.pepej.papi.text.Text.colorize
import com.pepej.papi.utils.Players
import org.bukkit.GameMode
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.scoreboard.DisplaySlot
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.BiConsumer


class Game(
    val joinedUsers: MutableSet<User> = mutableSetOf(),
    var state: GameState = GameState.WAIT,
    var startTime: Long = 0,
    val scoreboard: Scoreboard = Services.load(ScoreboardProvider::class.java).scoreboard,
    var alivePlayers: Int = 0,
    var spectate: Int = 0
) : Runnable {

    lateinit var gameTask: Task

    var timer = AtomicInteger(15)

    var updater = BiConsumer { player: Player, obj: ScoreboardObjective ->
        obj.displayName = "&bSkyWars"
        val map = instance.config.rootConfigNode.getNode("map").string
        val user = player.asUser()
        if (joinedUsers.contains(user)) {
            when (state) {
                GameState.WAIT -> {
                    obj.applyLines(
                        "&1",
                        "      &6$map",
                        "&2",
                        " &7Игроков: &a${joinedUsers.size}",
                        "&3",
                        "&4",
                        " &bSquareland.ru",
                    )
                }
                GameState.END -> {
                    obj.applyLines(
                        "&a Игра окончена!",
                        "&1",
                        "&7-----&a#1 pepej&7 <-----",
                        "&7-----&b#2PepejOffical&7-----",
                        "&2"
                    )
                }
                else -> {

                    obj.applyLines(
                        "&1",
                        "      &6$map",
                        "&5",
                        "   &b${TimeUtils.formatTime(timer.get())}",
                        "&2",
                        " &7Выживших: &a${UserManager.users.size - spectate}",
                        " &7Наблюдателей: &a$spectate",
                        " &7Убийств: &a${user.localKills}",
                        "&3",
                        "&4",
                        " &bSquareland.ru",
                    )
                }
            }
        } else {
            obj.unsubscribe(player)
        }
    }

    init {
        Schedulers.sync().runRepeating(this, 0, 20)
        Schedulers.sync().runRepeating({ _ ->
                for (player in Players.all()) {
                    val obj = provideForPlayer(player).getOrNull(SCOREBOARD_KEY)

                    if (obj != null) {
                        updater.accept(player, obj)

                    }
                }
            }, 3, 3
        )


    }


    override fun run() {
        if (state == GameState.PLAY) {
            timer.getAndDecrement()
            if (timer.get() <= 0) {
                end()

            }
        }
    }

    fun join(user: User, island: Island) {
        var old: Island? = null
        if (user.island != null) {
            old = user.island!!
            leave(user)
        }

        user.island = island
        island.users.add(user)
        if (old != null) {
            old.users.forEach {
                user.toPlayer.hidePlayer(instance, it.toPlayer)
                user.toPlayer.showPlayer(instance, it.toPlayer)
            }
            island.users.forEach {
                user.toPlayer.hidePlayer(instance, it.toPlayer)
                user.toPlayer.showPlayer(instance, it.toPlayer)
            }
        } else {
            for (isl in Island.islands) {
                isl.users.forEach {
                    user.toPlayer.hidePlayer(instance, it.toPlayer)
                    user.toPlayer.showPlayer(instance, it.toPlayer)
                }
            }
        }

        val objective = scoreboard.createPlayerObjective(user.toPlayer, "null", DisplaySlot.SIDEBAR)
        provideForPlayer(user.toPlayer).put(SCOREBOARD_KEY, objective)
        joinedUsers.add(user)
        user.toPlayer.msg(Players.MessageType.ANNOUNCEMENT, "Теперь Вы играете за Остров ${island.id}")
        if (joinedUsers.size == 2) {
            start()
        }
    }

    fun leave(user: User) {
        joinedUsers.remove(user)
        if (user.island != null) {
            user.island!!.users.remove(user)
            user.island = null
            if (state == GameState.PLAY) {
                alivePlayers = UserManager.users.filter { it.island != null }.count()
                if (aliveIslands <= 1) {
                    end()
                }
            }
        }
    }


    fun start() {
        if (state == GameState.WAIT) {
            state = GameState.START
            val timer = AtomicInteger(5)
            Schedulers.async().runRepeating({ task: Task ->

                if (timer.get() == 0) {
                    task.close()
                    return@runRepeating
                }

                for (user in joinedUsers) {
                    user.toPlayer.sendTitle("", colorize("&bНачало через $timer"), 5, 15, 5)
                }
                timer.decrementAndGet()


            }, 0, 20)
            Schedulers.sync().runLater({ loadChunks() }, 40)
            Schedulers.sync().runLater({ startTask() }, 5, TimeUnit.SECONDS)

        }
    }


    fun end() {
        state = GameState.END
        joinedUsers.map { it.toPlayer }.forEach { it.msg("Игра окончена") }
        joinedUsers.forEach(this::leave)
        Schedulers.async().runLater({ state = GameState.WAIT}, 10, TimeUnit.SECONDS)
    }

    private fun startTask() {
        startTime = System.currentTimeMillis()
        state = GameState.PLAY
        spreadInTeams()
        var alive = 0
        Island.islands.forEach { island ->
            var spawnIndex = 0
            island.users.forEach {
                it.toPlayer.apply {
                    closeInventory()
                    itemOnCursor = null
                    noDamageTicks = 400
                    fallDistance = 0.0f
                    teleport(island.spawns[spawnIndex].position.toLocation())
                    saturation = 10.0f
                    foodLevel = 20
                    getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue = 20.0
                    health = 20.0
                    fireTicks = 0
                    gameMode = GameMode.SURVIVAL
                }
                if (it.activeKit != 0) {
                    Kit.kits.find { kit -> kit.id == it.activeKit }?.equip(it)
                }

                alive += 1
                spawnIndex += 1
            }
        }
        alivePlayers = alive
    }

    private fun loadChunks() {
        Island.islands.forEach { island ->
            island.spawns.forEach {
                it.position.toLocation().world.getChunkAt(it.position.toLocation()).load()
            }
        }
    }

    private val aliveIslands: Int
        get() = Island.islands.filter { it.users.isNotEmpty() }.count()


    private fun spreadInTeams() {
        for (user in UserManager.users.filter { it.island == null }) {
            val randomIsland =
                RandomSelector.uniform(Island.islands.filterNot { it.users.size >= it.spawns.size }).pick()
            join(user, randomIsland)
        }
    }
}