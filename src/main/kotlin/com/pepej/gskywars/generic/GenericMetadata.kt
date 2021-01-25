package com.pepej.gskywars.generic

import com.pepej.gskywars.model.User
import com.pepej.papi.menu.Menu
import com.pepej.papi.metadata.MetadataKey
import com.pepej.papi.scoreboard.ScoreboardObjective

object GenericMetadata {
    val SCOREBOARD_KEY: MetadataKey<ScoreboardObjective> = MetadataKey.create("scoreboard", ScoreboardObjective::class.java)
    val LAST_ATTACKER_KEY: MetadataKey<User> = MetadataKey.create("last-attacker", User::class.java)
    val LAST_ATTACKED_KEY: MetadataKey<User> = MetadataKey.create("last-attacked", User::class.java)
    val CURRENT_SPEED_KEY: MetadataKey<Int> = MetadataKey.createIntegerKey("current-speed")
    val NIGHT_VISION_KEY: MetadataKey<Boolean> = MetadataKey.createBooleanKey("night-vision")
    val HIDE_SPECTATORS_KEY: MetadataKey<Boolean> = MetadataKey.createBooleanKey("hide-spectators")
    val PREVIOUS_MENU: MetadataKey<Menu> = MetadataKey.create("last-menu", Menu::class.java)

}