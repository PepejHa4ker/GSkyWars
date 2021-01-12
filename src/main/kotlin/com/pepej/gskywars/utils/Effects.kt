package com.pepej.gskywars.utils


import com.pepej.papi.math.GenericMath.*
import com.pepej.papi.math.TrigonometricMath.*
import com.pepej.papi.math.matrix.Matrix4f
import com.pepej.papi.math.vector.Vector3f
import com.pepej.papi.random.RandomSelector
import com.pepej.papi.reflect.ServerReflection
import com.pepej.papi.scheduler.Schedulers
import com.pepej.papi.scheduler.Task
import com.pepej.papi.utils.Players
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import java.lang.reflect.Method
import kotlin.math.max


object Effects {


    private var GET_ID_METHOD: Method
    private var  GET_HANDLE_METHOD: Method

    init {
        val entityClass = ServerReflection.nmsClass("Entity")
        GET_ID_METHOD = entityClass.getDeclaredMethod("getId")
        GET_ID_METHOD.isAccessible = true

        val craftEntityClass = ServerReflection.obcClass("entity.CraftEntity")
        GET_HANDLE_METHOD = craftEntityClass.getDeclaredMethod("getHandle")
        GET_HANDLE_METHOD.isAccessible = true

    }

    private fun getEntityId(entity: Entity): Int {
        val handle = GET_HANDLE_METHOD.invoke(entity)
        return GET_ID_METHOD.invoke(handle) as Int
    }


    private val COLORS = listOf(
        Color.AQUA,
        Color.BLACK,
        Color.GRAY,
        Color.BLUE,
        Color.FUCHSIA,
        Color.GREEN,
        Color.LIME,
        Color.MAROON,
        Color.NAVY,
        Color.OLIVE,
        Color.ORANGE,
        Color.PURPLE,
        Color.RED,
        Color.SILVER,
        Color.TEAL,
        Color.WHITE,
        Color.YELLOW
    )

    private fun calculateAngleBetweenDirections(at: Vector3f, to: Vector3f): Matrix4f {
        return Matrix4f.createLookAt(Vector3f.ZERO, to.sub(at), Vector3f.UP).invert()
    }

    private fun canRayPassBlock(block: Block): Boolean {
        return block.type == Material.GLASS
                || block.type == Material.AIR
                || block.type == Material.FIRE
                || block.type == Material.STAINED_GLASS
                || block.type == Material.STAINED_GLASS_PANE
                || block.type == Material.THIN_GLASS
                || block.type == Material.BARRIER
    }

    private fun spawnColoredRedstoneParticle(location: Location, color: Color) {
        val r = max(1.0E-10, (color.red div 255.0))
        val g = (color.green div 255.0)
        val b = (color.blue div 255.0)
        location.world.spawnParticle(Particle.REDSTONE, location, 0, r, g, b, 1.0)
    }

    fun drillEffect(
        at: Location,
        to: Location,
        circleParts: Int,
        startRadius: Double,
        endRadius: Double,
        atHit: (Location) -> Unit,
    ) {

        var depth = 0.0
        val matrix = calculateAngleBetweenDirections(to.toVector3f(), at.toVector3f())
        val radDiff = (startRadius - endRadius) / (at distance to)
        var rad = startRadius
        ray@ while (rad >= endRadius) {
            println(radDiff)
            rad -= radDiff
            depth += 1.0

            var ang = 0.0
            circle@ while (ang <= 2 * PI) {
                ang += (2 * PI / circleParts)
                val x = cos(ang) * rad
                val y = sin(ang) * rad
                val newLoc = at.clone().add(matrix.transform(x, y, depth, 1.0).toBukkitVector())
                if (!canRayPassBlock(newLoc.world.getBlockAt(newLoc))) {
                    break@ray
                }

                spawnColoredRedstoneParticle(
                    newLoc,
                    RandomSelector.weighted(COLORS) { if (it == Color.RED) 1.0 else 5.0 }.pick()
                )

            }
        }
        atHit(to)
    }


    fun shootEffectTask(
        location: Location,
        stepSize: Double,
        steps: Double,
        interval: Long,
        particle: Particle
    ): Task {
        val vector = location.direction.toVector3d()
        val step = 0.0
        return Schedulers.sync().runRepeating({ task ->
            if (step >= steps) {
                task.stop()
                return@runRepeating
            }
            step inc stepSize
            Players.spawnParticle(location.clone().add(vector.mul(step).toBukkitVector()), particle)

        }, 0, interval)
    }
}