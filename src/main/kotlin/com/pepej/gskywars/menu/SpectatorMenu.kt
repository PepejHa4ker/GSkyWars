package com.pepej.gskywars.menu

import com.pepej.gskywars.GSkyWars.Companion.instance
import com.pepej.gskywars.generic.GenericItems
import com.pepej.gskywars.generic.GenericMetadata
import com.pepej.gskywars.managers.UserManager
import com.pepej.gskywars.model.Head
import com.pepej.gskywars.model.Kit
import com.pepej.gskywars.utils.*
import com.pepej.papi.item.ItemStackBuilder
import com.pepej.papi.menu.Menu
import com.pepej.papi.menu.scheme.MenuScheme
import com.pepej.papi.menu.scheme.StandardSchemeMappings
import com.pepej.papi.metadata.Metadata
import com.pepej.papi.utils.Players
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.concurrent.atomic.AtomicInteger

class SpectatorMenu(player: Player) : Menu(player, 3, "Меню наблюдателя") {
    companion object {

        private val SPEED_SETTINGS_SCHEME: MenuScheme = MenuScheme()
            .mask("000010000")
            .maskEmpty(2)

        private val RESET_SPEED_SCHEME: MenuScheme = MenuScheme()
            .mask("000001000")
            .maskEmpty(2)

        private val ABILITY_SCHEME: MenuScheme = MenuScheme()
            .mask("010000010")
            .maskEmpty(2)

        private val PLAYERS_HEADS_SCHEME: MenuScheme = MenuScheme()
            .maskEmpty(1)
            .mask("111111111")
            .mask("111111111")

    }

    override fun redraw() {
        fillNullableWith(StandardSchemeMappings.STAINED_GLASS.get(7).get())
        val meta = Metadata.provideForPlayer(player)
        var nightVisionEnabled = meta.getOrDefault(GenericMetadata.NIGHT_VISION_KEY, false)
        var hideSpectatorEnabled = meta.getOrDefault(GenericMetadata.HIDE_SPECTATORS_KEY, false)
        val resetSpeedPopulator = RESET_SPEED_SCHEME.newPopulator(this)
        val speedPopulator = SPEED_SETTINGS_SCHEME.newPopulator(this)
        val playerPopulator = PLAYERS_HEADS_SCHEME.newPopulator(this)
        val abilityPopulator = ABILITY_SCHEME.newPopulator(this)
        val currentSpeed = AtomicInteger(meta.getOrDefault(GenericMetadata.CURRENT_SPEED_KEY, 0))

        val nightVision =
            ItemStackBuilder.head(Head.findByName(if (nightVisionEnabled) "Midnight blue" else "Night Globe").texture)
                .nameClickable("${if (nightVisionEnabled) "&a" else "&c"}Ночное зрение")
                .loreClickable("чтобы включить/выключить ночное зрение")
                .lore(if (nightVisionEnabled) "&aВключено" else "&cВыключено")
                .buildConsumer {
                    val player = it.whoClicked as Player
                    if (!nightVisionEnabled) {
                        meta.put(GenericMetadata.NIGHT_VISION_KEY, true)
                        player.addPotionEffect(PotionEffect(PotionEffectType.NIGHT_VISION, 12000000, 0, false, false))
                    } else {
                        player.removePotionEffect(PotionEffectType.NIGHT_VISION)
                        nightVisionEnabled = false
                        meta.put(GenericMetadata.NIGHT_VISION_KEY, false)
                    }
                    redraw()
                }

        val hideSpectators =
            ItemStackBuilder.head(Head.findByName(if (hideSpectatorEnabled) "Explosive Crate Active" else "Explosive Crate").texture)
                .nameClickable("${if (hideSpectatorEnabled) "&a" else "&c"}Скрыть наблюдателей")
                .loreClickable("чтобы показать/скрыть наблюдателей")
                .lore(if (hideSpectatorEnabled) "&aПоказываются" else "&cСкрыты")
                .buildConsumer {
                    val player = it.whoClicked as Player
                    if (!hideSpectatorEnabled) {
                        meta.put(GenericMetadata.HIDE_SPECTATORS_KEY, true)
                        for (user in UserManager.users.filter { u -> u.spectator && u != player.asUser() }) {
                            player.hidePlayer(instance, user.toPlayer)
                        }
                    } else {
                        for (user in UserManager.users.filter { u -> u.spectator && u != player.asUser() }) {
                            player.showPlayer(instance, user.toPlayer)
                        }
                        hideSpectatorEnabled = false
                        meta.put(GenericMetadata.HIDE_SPECTATORS_KEY, false)
                    }
                    redraw()

                }

        abilityPopulator.apply {
            accept(nightVision)
            accept(hideSpectators)
        }
        for (user in UserManager.users.filterNot { it.spectator }) {
            playerPopulator.accept(
                ItemStackBuilder.of(SquarelandApi.getSkull(user.username))
                    .nameClickable("&7${user.username}")
                    .loreRightClickable("телепортироваться к игроку")
                    .loreLeftClickable("открыть меню репутаций")
                    .lore(
                        "  &bЗдоровье: &a${
                            (user.toPlayer.health / user.toPlayer.getAttribute(Attribute.GENERIC_MAX_HEALTH).value * 100).round(
                                2
                            )
                        }%"
                    )
                    .lore("  &bУровень еды: &a${user.toPlayer.foodLevel * 5}%")
                    .lore("  &bТекущий кит: &a${Kit.kits.find { it.id == user.activeKit }?.name ?: "Не выбрано"}")

                    .buildConsumer({
                        it.whoClicked.apply {
                            teleport(user.toPlayer.location)
                            closeInventory()
                        }
                    }, { VoteMenu().open() })
            )
        }
        val firstLvl = ItemStackBuilder.head(Head.findByName("Green").texture)
            .nameClickable("&aСкорость I")
            .buildConsumer {
                val player = it.whoClicked as Player
                player.flySpeed = 0.35f
                player.walkSpeed = 0.25f
                currentSpeed.set(1)
                meta.put(GenericMetadata.CURRENT_SPEED_KEY, 1)
                redraw()

            }

        val secondLvl = ItemStackBuilder.head(Head.findByName("Yellow").texture)
            .nameClickable("&eСкорость II")
            .buildConsumer {
                val player = it.whoClicked as Player
                player.flySpeed = 0.5f
                player.walkSpeed = 0.3f
                currentSpeed.set(2)
                meta.put(GenericMetadata.CURRENT_SPEED_KEY, 2)
                redraw()
            }


        val thirdLvl = ItemStackBuilder.head(Head.findByName("Magenta").texture)
            .nameClickable("&dСкорость III")
            .buildConsumer {
                val player = it.whoClicked as Player
                player.flySpeed = 0.7f
                player.walkSpeed = 0.4f
                currentSpeed.set(3)
                meta.put(GenericMetadata.CURRENT_SPEED_KEY, 3)
                redraw()

            }

        val fourthLvl = ItemStackBuilder.head(Head.findByName("Purple").texture)
            .nameClickable("&5Скорость IV")
            .buildConsumer {
                val player = it.whoClicked as Player
                player.flySpeed = 0.8f
                player.walkSpeed = 0.5f
                currentSpeed.set(4)
                meta.put(GenericMetadata.CURRENT_SPEED_KEY, 4)
                redraw()
            }
        val fifthLvl = ItemStackBuilder.head(Head.findByName("Red").texture)
            .nameClickable("&cСкорость V")

            .buildConsumer {
                val player = it.whoClicked as Player
                player.flySpeed = 0.9f
                player.walkSpeed = 0.6f
                currentSpeed.set(5)
                meta.put(GenericMetadata.CURRENT_SPEED_KEY, 5)
                redraw()

            }

        val resetSpeed = ItemStackBuilder.head(Head.findByName("White").texture)
            .nameClickable("&6Сбросить скорость")
            .buildConsumer {
                val player = it.whoClicked as Player
                Players.resetFlySpeed(player)
                Players.resetWalkSpeed(player)
                currentSpeed.set(0)
                meta.put(GenericMetadata.CURRENT_SPEED_KEY, 0)
                redraw()

            }

        resetSpeedPopulator.accept(resetSpeed)

        when (currentSpeed.get()) {
            0 -> {
                speedPopulator.accept(firstLvl)
            }
            1 -> {
                speedPopulator.accept(secondLvl)

            }
            2 -> {
                speedPopulator.accept(thirdLvl)

            }
            3 -> {
                speedPopulator.accept(fourthLvl)

            }
            4 -> {
                speedPopulator.accept(fifthLvl)

            }
            5 -> {
                speedPopulator.accept(firstLvl)

            }

        }
    }

    private inner class VoteMenu(override val previous: Menu = SpectatorMenu(player)) :
        InnerMenu(player, 3, "Голосование") {

        private val scheme: MenuScheme = MenuScheme()
            .maskEmpty(1)
            .mask("111111111")
            .mask("111111111")

        override fun redraw() {
            super.redraw()
            val userPlayer = player.asUser()
            fillNullableWith(
                ItemStackBuilder.of(Material.STAINED_GLASS_PANE).name("&c").durability(7).buildItem().build()
            )
            setItem(4, GenericItems.VOTE_MENU)
            val playerHeadsPopulator = scheme.newPopulator(this)
            for (user in UserManager.users.filterNot { it.spectator }) {
                playerHeadsPopulator.accept(
                    ItemStackBuilder.of(SquarelandApi.getSkull(user.username))
                        .name("&7${user.username}")
                        .lore("&aПКМ - Лайк")
                        .lore("&cЛКМ - Дизлайк")
                        .lore("  &bТекущая репутация: ${user.reputation}")
                        .buildConsumer({
                            if (userPlayer.canVote) {
                                userPlayer.lastVoteTimeStamp = System.currentTimeMillis()
                                user.reputation += 1
                                player.msg(
                                    Players.MessageType.ANNOUNCEMENT,
                                    "Репутация игроку &6${user.username}&a успешно повышена, текущая: &6${user.reputation}"
                                )
                                player.msg(
                                    "В следующий раз Вы сможете проголоовать через&b${
                                        TimeUtils.formatTime(
                                            userPlayer.voteDelay.toInt()
                                        )
                                    }"
                                )
                                user.toPlayer.msg(
                                    Players.MessageType.ANNOUNCEMENT,
                                    "Игрок &6${player.name}&a повысил Вам репутацию"
                                )
                                redraw()
                            } else {
                                player.msg(
                                    Players.MessageType.WARNING,
                                    "Вы не сможете голосовать еще${TimeUtils.formatTime(userPlayer.voteDelay.toInt())}"
                                )
                            }
                        }
                        ) {
                            if (userPlayer.canVote) {
                                userPlayer.lastVoteTimeStamp = System.currentTimeMillis()
                                user.reputation -= 1
                                player.msg(
                                    Players.MessageType.ANNOUNCEMENT,
                                    "Репутация игроку &6${user.username}&a успешно понижена, текущая: &6${user.reputation}"
                                )
                                player.msg(
                                    "В следующий раз Вы сможете проголоовать через&b${
                                        TimeUtils.formatTime(
                                            userPlayer.voteDelay.toInt()
                                        )
                                    }"
                                )
                                user.toPlayer.msg(
                                    Players.MessageType.ANNOUNCEMENT,
                                    "Игрок &6${player.name}&a понизил Вам репутацию"
                                )
                                redraw()
                            } else {
                                player.msg(
                                    Players.MessageType.WARNING,
                                    "Вы не сможете голосовать еще${TimeUtils.formatTime(userPlayer.voteDelay.toInt())}"
                                )
                            }
                        }
                )
            }
        }
    }
}