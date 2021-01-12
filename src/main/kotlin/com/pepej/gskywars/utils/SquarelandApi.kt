package com.pepej.gskywars.utils

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import com.pepej.papi.Services
import com.pepej.papi.gson.GsonProvider.parser
import com.pepej.papi.npc.Npc
import com.pepej.papi.npc.NpcFactory
import com.pepej.papi.serialize.Position
import com.pepej.papi.text.Text.colorize
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


object SquarelandApi {

    private fun parseProfile(player: String): GameProfile?  {
        val url = URL("http://auth.squareland.ru/profile?user=$player")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "GET"
        conn.connect()
        BufferedReader(InputStreamReader(url.openStream())).use { reader ->
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val parse = parser().parse(line).asJsonObject
                return try {
                    val profile = GameProfile(parse["id"].asString.toId(), parse["name"].asString)
                    profile.properties.put("textures", Property("textures", parse["properties"].asJsonArray.get(0).asJsonObject["value"].asString,""))
                    profile
                } catch (e: Exception) {
                    null
                }



            }
        }

        return null

    }

    fun spawnPlayerNpc(position: Position, player: Player): Npc? {
        val npcFactory = Services.load(NpcFactory::class.java)
        return npcFactory.spawnNpc(position.toLocation(), colorize(player.displayName), parseProfile(player.name)?.properties?.get("textures")?.first()?.value ?: return null, "")
    }



    fun getSkull(player: String): ItemStack {
        val head = ItemStack(Material.SKULL_ITEM, 1, 3.toShort())
        val headMeta = head.itemMeta as SkullMeta
        val profile = parseProfile(player) ?: return head
        val profileField = headMeta.javaClass.getDeclaredField("profile")
        profileField.isAccessible = true
        profileField.set(headMeta, profile)
        head.itemMeta = headMeta
        return head
    }

}