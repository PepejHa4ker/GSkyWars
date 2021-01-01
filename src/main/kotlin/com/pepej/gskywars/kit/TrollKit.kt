package com.pepej.gskywars.kit

import com.pepej.gskywars.model.Kit
import com.pepej.gskywars.model.User
import com.pepej.papi.item.ItemStackBuilder
import com.pepej.papi.terminable.TerminableConsumer
import org.bukkit.Material

class TrollKit : Kit(
    2,
    "Тролль",
    250,
    false,
    ItemStackBuilder.of(Material.FIREWORK)
        .name("&6Хлопушка тролля")
        .lore("&7Карманная хлопушка тролля для издевательства над игроками")
        .build()

) {
    override fun equip(user: User) {
        //
    }

    override fun setup(consumer: TerminableConsumer) {
    }

    init {
        kits.add(this)
    }
}