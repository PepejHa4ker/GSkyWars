package com.pepej.gskywars.config

import com.pepej.papi.config.ConfigFactory
import com.pepej.papi.time.DurationParser
import ninja.leaping.configurate.ConfigurationNode
import java.io.File
import java.time.Duration

class Config(file: File) {
    val rootConfigNode: ConfigurationNode
    val voteDelay: Duration

    init {
        rootConfigNode = ConfigFactory.gson().load(file)
        voteDelay = DurationParser.parse(rootConfigNode.getNode("votes").getNode("time-per-vote").string)

    }
}