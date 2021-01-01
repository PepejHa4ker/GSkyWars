package com.pepej.gskywars.kit

import com.pepej.gskywars.model.Kit
import com.pepej.gskywars.model.User
import com.pepej.papi.item.ItemStackBuilder
import com.pepej.papi.terminable.TerminableConsumer
import org.bukkit.Material

class GiantKit : Kit(
    1,
    "Гигант",
    100,
    true,
    ItemStackBuilder.of(Material.LEATHER_CHESTPLATE)
        .name("&dКуртка циклопа")
        .lore("&7Когда-то её носил сам циклоп, которого убил Одиссей")
        .build()) {

    override fun equip(user: User) {
        //
    }

    override fun setup(consumer: TerminableConsumer) {
    }

    init {
        kits.add(this)
    }
}