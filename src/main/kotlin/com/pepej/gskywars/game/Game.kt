package com.pepej.gskywars.game

import com.pepej.gskywars.GSkyWars.Companion.instance
import com.pepej.gskywars.api.events.GSkyWarsGameEndEvent
import com.pepej.gskywars.api.events.GSkyWarsGameStartEvent
import com.pepej.gskywars.api.events.GSkyWarsUserJoinEvent
import com.pepej.gskywars.api.events.GSkyWarsUserLeaveEvent
import com.pepej.gskywars.generator.LootGenerator
import com.pepej.gskywars.generator.StandardLootGenerator
import com.pepej.gskywars.generic.GenericItems
import com.pepej.gskywars.generic.GenericMetadata
import com.pepej.gskywars.generic.GenericMetadata.LAST_ATTACKER_KEY
import com.pepej.gskywars.generic.GenericMetadata.SCOREBOARD_KEY
import com.pepej.gskywars.managers.UserManager
import com.pepej.gskywars.model.*
import com.pepej.gskywars.utils.*
import com.pepej.papi.Services
import com.pepej.papi.bossbar.BossBar
import com.pepej.papi.bossbar.BossBarColor
import com.pepej.papi.bossbar.BossBarFactory
import com.pepej.papi.events.Events
import com.pepej.papi.events.Events.subscribe
import com.pepej.papi.hologram.HologramLine
import com.pepej.papi.hologram.individual.IndividualHologramFactory
import com.pepej.papi.item.ItemStackBuilder
import com.pepej.papi.messaging.bungee.BungeeCord
import com.pepej.papi.metadata.ExpiringValue
import com.pepej.papi.metadata.Metadata.provideForPlayer
import com.pepej.papi.random.RandomSelector
import com.pepej.papi.scheduler.Schedulers
import com.pepej.papi.scheduler.Task
import com.pepej.papi.scoreboard.Scoreboard
import com.pepej.papi.scoreboard.ScoreboardObjective
import com.pepej.papi.scoreboard.ScoreboardProvider
import com.pepej.papi.terminable.TerminableConsumer
import com.pepej.papi.terminable.module.TerminableModule
import com.pepej.papi.text.Text.colorize
import com.pepej.papi.utils.Players
import net.md_5.bungee.api.chat.TextComponent
import ninja.leaping.configurate.ConfigurationNode
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.*
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.scoreboard.DisplaySlot
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.BiConsumer
import kotlin.random.Random


class Game(
    var state: GameState = GameState.WAIT,
    var startTime: Long = 0,
    private val scoreboard: Scoreboard = Services.load(ScoreboardProvider::class.java).scoreboard,
    private val config: ConfigurationNode = instance.config.rootConfigNode.getNode("game"),
    private var timer: AtomicInteger = AtomicInteger(instance.config.timer.seconds.toInt()),
    private val lootGenerator: LootGenerator = StandardLootGenerator(),
    private val bossbar: BossBar = Services.load(BossBarFactory::class.java).newBossBar(),
    var alivePlayers: Int = 0,
) : Runnable, TerminableModule {

    companion object {
        private val COLORS = listOf(
            ChatColor.BLUE,
            ChatColor.GREEN,
            ChatColor.DARK_GREEN,
            ChatColor.AQUA,
            ChatColor.DARK_AQUA,
            ChatColor.YELLOW,
            ChatColor.GOLD,
        )
        private val BOSSBAR_COLORS = listOf(BossBarColor.BLUE, BossBarColor.GREEN, BossBarColor.PINK)

        private val MESSAGES = listOf(
            "&aОн был хорошей тренировочной грушей...",
            "&bПокойся с миром, нуб"
        )
    }

    private lateinit var color: ChatColor


    override fun setup(consumer: TerminableConsumer) {

        bossbar
            .color(BossBarColor.RED)
            .progress(1.0)
            .bindWith(consumer)

        subscribe(EntityDamageByEntityEvent::class.java, EventPriority.MONITOR)
            .filter { it.damager is Player && it.entity is Player }
            .handler {
                val attacker = (it.damager as Player).asUser()
                val victim = (it.entity as Player).asUser()
                if (attacker.spectator || victim.spectator) {
                    it.isCancelled = true
                    return@handler
                }

                if (state == GameState.WAIT || state == GameState.START) {
                    it.isCancelled = true
                    return@handler
                }

                if (attacker.island != null && victim.island != null) {
                    if (attacker.island?.users?.contains(victim) == true) {
                        it.isCancelled = true
                        return@handler
                    }

                    val attackerMeta = provideForPlayer(attacker.toPlayer)
                    val victimMeta = provideForPlayer(victim.toPlayer)

                    victimMeta.put(LAST_ATTACKER_KEY, ExpiringValue.of(attacker, 1, TimeUnit.MINUTES))
                    attackerMeta.put(LAST_ATTACKER_KEY, ExpiringValue.of(victim, 1, TimeUnit.MINUTES))
                }

            }
            .bindWith(consumer)
        subscribe(EntityDamageEvent::class.java)
            .handler {
                if (state == GameState.WAIT || state == GameState.START) {
                    it.isCancelled = true
                    return@handler
                }
            }
            .bindWith(consumer)
        subscribe(ProjectileLaunchEvent::class.java)
            .handler {
                val user = (it.entity.shooter as Player).asUser()
                if (user.activeTrail != 0) {
                    val activeTrail = Trail.all().find { t -> t.id == user.activeTrail } ?: return@handler
                    Schedulers.sync().runRepeating({ task ->
                        if (it.entity.isDead || it.entity.isOnGround || !it.entity.isValid) {
                            task.close()
                            return@runRepeating
                        }
                        Players.spawnParticle(it.entity.location, activeTrail.particle)
                    }, 0, 3)

                }
            }
            .bindWith(consumer)
        subscribe(PlayerJoinEvent::class.java)
            .handler {
                when (state) {
                    GameState.END -> {
                        setSpectator(it.player.asUser())
                    }
                    GameState.PLAY -> {
                        setSpectator(it.player.asUser())
                    }

                    else -> {
                        it.player.teleport(instance.config.waitinLocation)
                        joinRandomIsland(it.player.asUser())
                    }
                }
            }
            .bindWith(consumer)

        subscribe(BlockPlaceEvent::class.java)
            .handler {
                it.player.asUser().blocksPlaced += 1
            }
            .bindWith(consumer)

        subscribe(BlockBreakEvent::class.java)
            .handler {
                it.player.asUser().blocksBroken += 1
            }
            .bindWith(consumer)

        subscribe(PlayerInteractEvent::class.java)
            .filter { it.clickedBlock != null && !it.clickedBlock.isEmpty && it.clickedBlock.type == Material.CHEST }
            .handler { e ->
                val user = e.player.asUser()
                if (user.island != null) {
                    for (chest in user.island?.chests ?: return@handler) {
                        if (chest.position == Position.of(e.clickedBlock.location)) {
                            chest.opened = true
                        }
                    }
                }
            }

            .bindWith(consumer)

        subscribe(EntityShootBowEvent::class.java)
            .filter { it.entity is Player }
            .handler {
                (it.entity as Player).asUser().arrowsFired += 1
            }
            .bindWith(consumer)

        subscribe(PlayerDeathEvent::class.java)
            .handler {
                val hologramFactory = Services.load(IndividualHologramFactory::class.java)
                val user = it.entity.asUser()
                user.deathLocation = Position.of(it.entity.location)
                Schedulers.sync().runLater({ it.entity.spigot().respawn() }, 3)
                user.deaths += 1
                val meta = provideForPlayer(it.entity)
                val lastAttacker = meta.getOrNull(LAST_ATTACKER_KEY)
                if (lastAttacker != null) {
                    lastAttacker.kills += 1
                    lastAttacker.localKills += 1
                    lastAttacker.toPlayer.message("             &dВы убили &c${it.entity.name}")
                    val message = MESSAGES.random()
                    val hologram = hologramFactory.newHologram(
                        com.pepej.papi.serialize.Position.of(it.entity.location.add(0.0, 1.5, 0.0)),
                        listOf(HologramLine { message })
                    )
                    hologram.addExpiring(20, TimeUnit.SECONDS)
                    hologram.apply {
                        addViewer(it.entity)
                        addViewer(lastAttacker.toPlayer)
                    }
                    hologram.spawn()
                } else {
                    Players.all()
                        .forEach { p -> p.message("             &d${it.entity.name}&c самоубился..") }
                }
            }

        subscribe(PlayerRespawnEvent::class.java)
            .handler {
                val user = it.player.asUser()
                it.respawnLocation = user.deathLocation?.toLocation()
                if (state == GameState.PLAY || state == GameState.END) {
                    setSpectator(user)
                }
            }

    }

    private fun setSpectator(user: User) {
        user.spectator = true
        user.toPlayer.apply {
            allowFlight = true
            isFlying = true
            inventory.apply {
                setItem(0, GenericItems.SPECTATOR_ITEM)
                setItem(4, GenericItems.PROFILE_MENU)
            }
            msg("Теперь Вы наблюдатель")
            UserManager.users.filterNot { u -> u.spectator }
                .map { u -> u.toPlayer }
                .forEach { p -> p.hidePlayer(instance, player) }
            teleport(instance.config.spectatorLocation)

        }
    }


    private var updater = BiConsumer { player: Player, obj: ScoreboardObjective ->
        obj.displayName = "$color SkyWars"
        val map = config.getNode("map").string
        val user = player.asUser()
        when (state) {
            GameState.WAIT, GameState.START -> obj.applyLines(
                "&1",
                "      &c$map",
                "&2",
                "    &7Игроков: &d${Players.all().size}",
                "&3",
                "&4",
                "$color  squareland.ru",
            )

            GameState.PLAY -> obj.applyLines(
                "&1",
                "      &c$map",
                "&5",
                "      ${getTimerColor(timer.get()) + convertSecondsToMMSS(timer.get())}",
                "&2",
                " &7Выживших: &d${UserManager.users.filterNot { it.spectator }.count()}",
                " &7Наблюдателей: &d${UserManager.users.filter { it.spectator }.count()}",
                " &7Убийств: &d${user.localKills}",
                "&3",
                "&4",
                "$color  squareland.ru",
            )

            else -> obj.applyLines(listOf(
                listOf(
                    "&1",
                    "  &7 Игра окончена",
                    "&2"
                ), getTopMessage(2, true)).flatten())

        }
    }


    init {
        Schedulers.sync().runRepeating(this, 0, 20)
        Schedulers.sync().runRepeating({ _ ->
            color = COLORS.random()
            when (state) {
                GameState.END -> {
                    bossbar
                        .title("&dИгра окончена, победитель &7- &5${getTop(1).firstOrNull()?.username ?: "---"}")
                        .progress(1.0)
                    .color(BOSSBAR_COLORS.random())
                }
                GameState.PLAY -> {
                    bossbar
                        .title("$color В живых еще &a$aliveIslands$color островов")
                        .progress((timer.get().toDouble()) / instance.config.timer.seconds.toDouble())
                        .color(getTimerBossBarColor(timer.get()))
                }
                GameState.WAIT -> {
                    bossbar.title("$color Ожидание игроков")
                        .progress(1.0)
                }
                else -> {}
            }
                for (player in Players.all()) {
                    val obj = provideForPlayer(player).getOrNull(SCOREBOARD_KEY)

                    if (obj != null) {
                        updater.accept(player, obj)

                    }
                }
            }, 5, 15)


    }


    override fun run() {
        when (state) {
            GameState.WAIT -> {
                if (Players.all().size == Island.islands.map { it.spawns.size }.sum()) {
                    start()

                }
            }

            GameState.PLAY -> {
                timer.getAndDecrement()
                if (timer.get() <= 0 || UserManager.users.filterNot { it.spectator }.count() <= 1) {
                    end()

                }
            }
            else -> {
            }

        }
    }


    fun join(user: User, island: Island) {
        val uje = GSkyWarsUserJoinEvent(this, user)
        Events.call(uje)
        if (uje.isCancelled) {
            return
        }
        var old: Island? = null
        if (user.island != null) {
            old = user.island!!
            leave(user)
        }

        user.island = island
        user.toPlayer.inventory.clear()
        user.toPlayer.inventory.addItem(GenericItems.ISLAND_SELECTOR_ITEM)
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
        bossbar.addPlayer(user.toPlayer)
        user.toPlayer.message("&dТеперь Вы играете за Остров &c${island.id}")

    }

    fun leave(user: User) {
        Events.call(GSkyWarsUserLeaveEvent(this, user))
        bossbar.removePlayer(user.toPlayer)
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


    private fun start() {
        if (state == GameState.WAIT) {
            val lct = Schedulers.sync().runLater({ loadChunks() }, 40)
            val stt = Schedulers.sync().runLater({ startTask() }, 15, TimeUnit.SECONDS)
            val gse = GSkyWarsGameStartEvent(this)
            Events.call(GSkyWarsGameStartEvent(this))
            if (gse.isCancelled) {
                return
            }
            state = GameState.START
            val t = AtomicInteger(15)
            Schedulers.sync().runRepeating({ task: Task ->

                if (Players.all().size < Island.islands.map { it.spawns.size }.sum()) {
                    state = GameState.WAIT
                    lct.close()
                    stt.close()
                    task.close()
                    return@runRepeating
                }
                if (t.get() == 0) {
                    task.close()
                    return@runRepeating
                }
                bossbar
                    .progress(t.get().toDouble()/15.0)
                    .title("&7Начало через ${getStartTimerColor(t.get()) + t.get()}")
                    .color(getStartTimerBossBarColor(t.get()))

                for (player in Players.all()) {
                    player.exp = (t.get()/15.0).toFloat()
                    player.sendTitle(
                        "",
                        colorize("&7Начало через ${getStartTimerColor(t.get()) + t.get()}"),
                        5,
                        15,
                        5
                    )
                }
                t.getAndDecrement()

            }, 0, 20)


        }
    }


    private fun end() {
        state = GameState.END
        Schedulers.async().runLater({
            val bungee = Services.load(BungeeCord::class.java)
            Events.call(GSkyWarsGameEndEvent(this))
            printTopMessage(Island.islands.map { it.spawns.size }.sum())
            Schedulers.sync().runLater({
                Players.all().map { it.asUser() }.forEach {
                    leave(it)
                    bungee.connect(it.toPlayer, "lobby")
                }

                timer.set(instance.config.timer.seconds.toInt())
                state = GameState.WAIT

            }, 20)
        }, 10, TimeUnit.SECONDS)
    }


    private fun startTask() {
        startTime = System.currentTimeMillis()
        state = GameState.PLAY
        spreadInTeams()
        var alive = 0
        Island.islands.forEach { island ->
            island.chests.forEach {
                it.opened = false

            }

            var spawnIndex = 0
            island.users.forEach {
                it.games += 1
                it.spectator = false
                it.toPlayer.apply {
                    inventory.clear()
                    closeInventory()
                    itemOnCursor = null
                    allowFlight = false
                    isFlying = false
                    isCollidable = false
                    fallDistance = 0.0f
                    exp = 0.0f
                    teleport(island.spawns[spawnIndex].position)
                    saturation = 10.0f
                    noDamageTicks = 200
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
            fillChests(island)
        }


        alivePlayers = alive
    }

    private fun fillChests(island: Island) {
        val items = mutableListOf<ItemStack>()
        if (island.spawns.size == 1) {
            items.addAll(lootGenerator.basic())
        } else {

            for (i in 0..Island.islands.first().spawns.size) {
                items.random()
                items.addAll(thinLoot(lootGenerator.basic(), 0.9))
            }
        }
        val chests = island.chests
            .map { it.position.toLocation().block.state }
            .filterIsInstance<org.bukkit.block.Chest>()
            .map { it.inventory }
        for (chest in chests) {
            chest.clear()
        }

        items.forEachIndexed { idx, item ->
            chests[idx % chests.size].setItem(idx / chests.size, item)
        }


        for (element in chests) {
            randomFillInventory(element, element.contents.toList())
        }
        //TODO миддловые честы
    }

    private fun <T> thinLoot(list: List<T>, keepChance: Double): List<T> {
        val iterator = list.toMutableList().iterator()
        while (iterator.hasNext()) {
            iterator.next()
            if (Random.nextFloat() > keepChance) {
                iterator.remove()
            }
        }
        return list

    }

    private fun randomFillInventory(inv: Inventory, items: Iterable<ItemStack>) {
        val contents = arrayOfNulls<ItemStack>(inv.size)
        items.forEachIndexed { index, item ->
            contents[index] = item
        }
        contents.shuffle()
        inv.contents = contents
    }

    private fun getTopMessage(size: Int, scoreboard: Boolean = false): List<String> {
        val toReturn = mutableListOf<String>()
        toReturn.apply {
            if (!scoreboard) {
                add("&5&m----------------------------")
            }
            getTop(size).forEachIndexed { i, u ->
                add("${if (scoreboard) "" else "      " }&4#${(i + 1)} &d${u?.username ?: "####"} &7- &c${u?.localKills ?: "-"}")
            }
            if (!scoreboard) {
                add("&5&m----------------------------")
            }

        }
        return toReturn.toList()
    }

    private fun printTopMessage(size: Int) {
        Players.forEach { p ->
            getTopMessage(size).forEach { p.message(it) }

        }
    }

    private fun getTop(size: Int): List<User?> {
        val toReturn = mutableListOf<User>()
        val sorted = UserManager.users.sortedWith { o1, o2 -> o2.localKills.compareTo(o1.localKills) }
        for (i in 0 until size) {
            toReturn.add(sorted[i])
        }

        return toReturn
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

    fun joinRandomIsland(user: User) {
        val randomIsland =
            RandomSelector.uniform(Island.islands.filterNot { it.users.size >= it.spawns.size }).pick()
        if (user.island != null) {
            if (user.island == randomIsland) {
                joinRandomIsland(user) //рекурсивный вызов функции чтобы зайти еще раз

            }
        }
        join(user, randomIsland)

    }

    private fun getTimerColor(timer: Int): String = when (timer) {
        in 31..60 -> "&6"
        in 0..30 -> "&c"
        else -> "&a"
    }

    private fun getTimerBossBarColor(timer: Int): BossBarColor = when (timer) {
        in 31..60 -> BossBarColor.YELLOW
        in 0..40 -> BossBarColor.RED
        else -> BossBarColor.GREEN
    }

    private fun getStartTimerColor(timer: Int): String = when (timer) {
        in 11..15 -> "&a"
        in 6..10 -> "&d"
        else -> "&c"
    }

    private fun getStartTimerBossBarColor(timer: Int): BossBarColor = when (timer) {
        in 11..15 -> BossBarColor.GREEN
        in 6..10 -> BossBarColor.PURPLE
        else -> BossBarColor.RED

    }

    private fun spreadInTeams() {
        for (user in UserManager.users.filter { it.island == null }) {
            joinRandomIsland(user)
        }
    }

    private fun convertSecondsToMMSS(time: Int): String {
        val millis = time * 1000
        val df = SimpleDateFormat("mm:ss")
        return df.format(millis)
    }
}