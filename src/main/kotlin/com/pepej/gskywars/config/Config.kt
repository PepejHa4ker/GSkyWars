package com.pepej.gskywars.config

import com.pepej.gskywars.model.Position
import com.pepej.gskywars.model.serialization.Serializer
import com.pepej.papi.config.ConfigFactory
import com.pepej.papi.time.DurationParser
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import ninja.leaping.configurate.ConfigurationNode
import java.io.File
import java.time.Duration

class Config(file: File) {
    val rootConfigNode: ConfigurationNode
    val voteDelay: Duration
    val timer: Duration
    val spectatorLocation: Position
    val waitinLocation: Position

    init {
        rootConfigNode = ConfigFactory.gson().load(file)
        voteDelay = DurationParser.parse(rootConfigNode.getNode("time-per-vote").string)
        val gameConfig = rootConfigNode.getNode("game")
        timer = DurationParser.parse(gameConfig.getNode("timer").string)
        spectatorLocation = Serializer.deserialize(gameConfig.getNode("spectator-position").string)
        waitinLocation = Serializer.deserialize(gameConfig.getNode("waiting-position").string)
    }
}