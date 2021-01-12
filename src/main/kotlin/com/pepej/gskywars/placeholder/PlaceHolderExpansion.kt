package com.pepej.gskywars.placeholder

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.entity.Player

class PlaceHolderExpansion : PlaceholderExpansion() {
    override fun getIdentifier(): String {
        return "gskywars"
    }

    override fun getAuthor(): String {
        return "pepej"
    }

    override fun getVersion(): String {
        return "1.0.0"
    }

    override fun onPlaceholderRequest(player: Player?, params: String): String {
        return super.onPlaceholderRequest(player, params)
    }
}