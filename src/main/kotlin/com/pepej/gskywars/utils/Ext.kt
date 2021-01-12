package com.pepej.gskywars.utils

import com.pepej.gskywars.GSkyWars.Companion.instance
import com.pepej.gskywars.model.Position
import com.pepej.gskywars.model.User
import com.pepej.papi.item.ItemStackBuilder
import com.pepej.papi.math.GenericMath
import com.pepej.papi.math.vector.Vector3d
import com.pepej.papi.math.vector.Vector3f
import com.pepej.papi.math.vector.Vector4d
import com.pepej.papi.math.vector.Vector4f
import com.pepej.papi.menu.Item
import com.pepej.papi.text.Text
import com.pepej.papi.text.Text.colorize
import com.pepej.papi.utils.Players
import com.pepej.papi.utils.UndashedUuids
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import java.util.*


fun <T : CommandSender> T.msg(message: String) = Players.msg(this, message)
fun <T : CommandSender> T.msg(type: Players.MessageType, message: String) = Players.msg(this, type, message)
fun UUID.toStr(): String = UndashedUuids.toString(this)
fun String.toId(): UUID = UndashedUuids.fromString(this)
fun Player.asUser(): User = instance.userManager[this]
fun Location.toVector3f() = Vector3f(this.x, this.y, this.z)
fun Location.toVector3d() = Vector3d(this.x, this.y, this.z)
fun Vector.toVector3f() = Vector3f(this.x, this.y, this.z)
fun Vector.toVector3d() = Vector3d(this.x, this.y, this.z)
fun Vector3f.toBukkitLocation(world: World) = Location(world, this.x.toDouble(), this.y.toDouble(), this.z.toDouble())
fun Vector3d.toBukkitLocation(world: World) = Location(world, this.x, this.y, this.z)
fun Vector4f.toBukkitLocation(world: World) = Location(world, this.x.toDouble(), this.y.toDouble(), this.z.toDouble())
fun Vector4d.toBukkitLocation(world: World) = Location(world, this.x, this.y, this.z)
fun Vector3f.toBukkitVector() = Vector(this.x.toDouble(), this.y.toDouble(), this.z.toDouble())
fun Vector3d.toBukkitVector() = Vector(this.x, this.y, this.z)
fun Vector4f.toBukkitVector() = Vector(this.x.toDouble(), this.y.toDouble(), this.z.toDouble())
fun Vector4d.toBukkitVector() = Vector(this.x, this.y, this.z)
infix fun Int.inc(other: Int) = this.plus(other)
infix fun Int.inc(other: Float) = this.plus(other)
infix fun Int.inc(other: Double) = this.plus(other)
infix fun Float.inc(other: Float) = this.plus(other)
infix fun Double.inc(other: Double) = this.plus(other)
infix fun Float.inc(other: Double) = this.plus(other)
infix fun Double.inc(other: Float) = this.plus(other)
infix fun Int.dec(other: Int) = this.minus(other)
infix fun Int.dec(other: Double) = this.minus(other)
infix fun Int.dec(other: Float) = this.minus(other)
infix fun Float.dec(other: Float) = this.minus(other)
infix fun Double.dec(other: Double) = this.minus(other)
infix fun Float.dec(other: Double) = this.minus(other)
infix fun Double.dec(other: Float) = this.minus(other)
infix fun Int.mul(other: Int) = this.times(other)
infix fun Int.mul(other: Float) = this.times(other)
infix fun Int.mul(other: Double) = this.times(other)
infix fun Float.mul(other: Float) = this.times(other)
infix fun Float.mul(other: Double) = this.times(other)
infix fun Double.mul(other: Double) = this.times(other)
infix fun Double.mul(other: Float) = this.times(other)
infix fun Int.div(other: Int) = this.div(other)
infix fun Int.div(other: Float) = this.div(other)
infix fun Int.div(other: Double) = this.div(other)
infix fun Float.div(other: Float) = this.plus(other)
infix fun Float.div(other: Double) = this.plus(other)
infix fun Double.div(other: Double) = this.plus(other)
infix fun Double.div(other: Float) = this.plus(other)
infix fun Location.distance(other: Location) = this.distance(other)
fun Double.round(decimals: Int) = GenericMath.round(this, decimals)
fun Int.isOdd() = this % 2 == 0
fun Double.isOdd() = this % 2 == 0.0
fun Float.isOdd() = this % 2 == 0.0f
fun Item.toStack() = this.itemStack
fun ItemStack.toItem() = ItemStackBuilder.of(this).buildItem().build()
fun Entity.teleport(pos: Position) = this.teleport(pos.toLocation())
fun Location.asPosition() = Position.of(this)
fun Player.message(message: String) = this.sendMessage(colorize(message))



