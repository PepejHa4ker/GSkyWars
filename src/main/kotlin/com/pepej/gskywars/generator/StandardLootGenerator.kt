package com.pepej.gskywars.generator

import com.pepej.papi.item.ItemStackBuilder
import com.pepej.papi.random.RandomSelector
import com.pepej.papi.random.VariableAmount
import com.pepej.papi.text.Text.colorize
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.potion.Potion
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.potion.PotionType
import java.util.*
import java.util.function.Supplier
import kotlin.collections.ArrayList
import kotlin.random.Random

class StandardLootGenerator : LootGenerator {


    companion object {
        private val HELMETS = listOf(
            ItemStack(Material.DIAMOND_HELMET),
            ItemStack(Material.DIAMOND_HELMET),
            ItemStack(Material.GOLD_HELMET),
            ItemStack(Material.IRON_HELMET),
            ItemStack(Material.IRON_HELMET),
            ItemStack(Material.LEATHER_HELMET)
        )
        private val CHESTPLATES = listOf(
            ItemStack(Material.DIAMOND_CHESTPLATE),
            ItemStack(Material.DIAMOND_CHESTPLATE),
            ItemStack(Material.GOLD_CHESTPLATE),
            ItemStack(Material.IRON_CHESTPLATE),
            ItemStack(Material.IRON_CHESTPLATE),
            ItemStack(Material.LEATHER_CHESTPLATE)

        )
        private val LEGGINGS = listOf(
            ItemStack(Material.DIAMOND_LEGGINGS),
            ItemStack(Material.DIAMOND_LEGGINGS),
            ItemStack(Material.GOLD_LEGGINGS),
            ItemStack(Material.IRON_LEGGINGS),
            ItemStack(Material.IRON_LEGGINGS),
            ItemStack(Material.LEATHER_LEGGINGS),
        )
        private val BOOTS = listOf(
            ItemStack(Material.DIAMOND_BOOTS),
            ItemStack(Material.DIAMOND_BOOTS),
            ItemStack(Material.GOLD_BOOTS),
            ItemStack(Material.IRON_BOOTS),
            ItemStack(Material.IRON_BOOTS),
            ItemStack(Material.LEATHER_BOOTS),

        )

        private val PICKAXES = listOf(
            ItemStack(Material.STONE_PICKAXE),
            ItemStack(Material.IRON_PICKAXE),
            ItemStack(Material.GOLD_PICKAXE),
            ItemStack(Material.DIAMOND_PICKAXE)
        )
        private val AXES = listOf(
            ItemStack(Material.STONE_AXE),
            ItemStack(Material.IRON_AXE),
            ItemStack(Material.GOLD_AXE),
            ItemStack(Material.DIAMOND_AXE)
        )
        private val SWORDS = listOf(
            ItemStack(Material.STONE_SWORD),
            ItemStack(Material.IRON_SWORD),
            ItemStack(Material.GOLD_SWORD),
            ItemStack(Material.DIAMOND_SWORD)
        )
        private val SPADES = listOf(
            ItemStack(Material.STONE_SPADE),
            ItemStack(Material.IRON_SPADE),
            ItemStack(Material.GOLD_SPADE),
            ItemStack(Material.DIAMOND_SPADE)
        )
        private val BLOCKS = listOf(
            ItemStack(Material.STONE, VariableAmount.range(20.0, 40.0).flooredAmount),
            ItemStack(Material.WOOD, VariableAmount.range(20.0, 40.0).flooredAmount),
            ItemStack(Material.BRICK, VariableAmount.range(20.0, 40.0).flooredAmount),
            ItemStack(Material.COBBLESTONE, VariableAmount.range(20.0, 40.0).flooredAmount),
            ItemStack(Material.STONE, VariableAmount.range(20.0, 40.0).flooredAmount),
            ItemStack(Material.DIRT, VariableAmount.range(20.0, 40.0).flooredAmount)
        )
        private val FOOD = listOf(
            ItemStack(Material.CARROT_ITEM, VariableAmount.range(8.0, 32.0).flooredAmount),
            ItemStack(Material.APPLE, VariableAmount.range(8.0, 32.0).flooredAmount),
            ItemStack(Material.BREAD, VariableAmount.range(8.0, 32.0).flooredAmount),
            ItemStack(Material.BAKED_POTATO, VariableAmount.range(8.0, 32.0).flooredAmount),
            ItemStack(Material.COOKED_FISH, VariableAmount.range(8.0, 32.0).flooredAmount),
            ItemStack(Material.COOKED_BEEF, VariableAmount.range(8.0, 32.0).flooredAmount),
            ItemStack(Material.COOKED_CHICKEN, VariableAmount.range(8.0, 32.0).flooredAmount)
        )

        private val POTIONS = listOf(Supplier {
            val low: Boolean = Random.nextBoolean()
            val p = ItemStack(Material.SPLASH_POTION)
            val pMeta = p.itemMeta as PotionMeta
            pMeta.displayName = colorize("&bЗелье Скорости")
            pMeta.addCustomEffect(
                PotionEffect(PotionEffectType.SPEED, (if (low) 60 else 30) * 20, if (low) 0 else 1),
                true
            )
            p.itemMeta = pMeta
            p
        }, Supplier {
            val low: Boolean = Random.nextBoolean()
            val p = ItemStack(Material.SPLASH_POTION)
            val pMeta = p.itemMeta as PotionMeta
            pMeta.displayName = colorize("&сЗелье Регенерации")
            pMeta.addCustomEffect(
                PotionEffect(
                    PotionEffectType.REGENERATION,
                    (if (low) 33 else 16) * 20,
                    if (low) 0 else 1
                ), true
            )
            p.itemMeta = pMeta
            p
        }, Supplier { ItemStack(Material.POTION, 1, 8261.toShort()) })
    }

    override fun basic(): List<ItemStack> {
        val items = ArrayList<ItemStack>(32)
        items.apply {
            add(RandomSelector.weighted(HELMETS) { getWeightForType(it.type) }.pick())
            add(RandomSelector.weighted(CHESTPLATES) { getWeightForType(it.type) }.pick())
            add(RandomSelector.weighted(LEGGINGS) { getWeightForType(it.type) }.pick())
            add(RandomSelector.weighted(BOOTS) { getWeightForType(it.type) }.pick())
        }
        if (Random.nextFloat() < 0.05) {
            items.removeAt(Random.nextInt(4))
        }

        if (Random.nextFloat() < 0.05) {
            items.add(listOf(HELMETS, CHESTPLATES, LEGGINGS, BOOTS).random().random())

        }

        var rnd: Float
        for (item in items) {
            rnd = Random.nextFloat()
            val type = item.type
            if (isDiamond(type)) {
                if (rnd < 0.1F) {
                    item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                }
            } else if (isIron(type)) {
                if (rnd < 0.03f) {
                    item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                } else if (rnd < 0.1f) {
                    item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                }
            } else if (isGolden(type)) {
                if (rnd < 0.3) {
                    item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                } else if (rnd < 0.1) {
                    item.addUnsafeEnchantment(Enchantment.PROTECTION_FIRE, 3)
                }
            } else if (isLeather(type)) {

                if (rnd < 0.3) {
                    item.addUnsafeEnchantment(Enchantment.DURABILITY, 10)
                } else if (rnd < 0.02) {
                    item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                }
            } else if (rnd < 0.02f) {
                item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
            } else if (rnd < 0.05f) {
                item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
            } else if (rnd < 0.15f) {
                item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
            }
            if (Random.nextFloat() < 0.05f) {
                item.addUnsafeEnchantment(Enchantment.PROTECTION_FIRE, 1)
            }
        }

        rnd = Random.nextFloat()
        val sword = SWORDS.random()
        if (rnd < 0.3) {
            if (Random.nextFloat() < 0.1f) {
                sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1)
            }
        } else if (rnd < 0.6) {
            if (Random.nextFloat() < 0.15) {
                sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1)
            }

            if (Random.nextFloat() < 0.04) {
                sword.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 1)
            }
        } else {
            rnd = Random.nextFloat()
            if (rnd < 0.05f) {
                sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2)
            } else if (rnd < 0.4f) {
                sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1)
            }

            if (Random.nextFloat() < 0.04f) {
                sword.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 1)
            }
        }

        items.add(sword)
        if (Random.nextFloat() < 0.1f) {
            items.add(ItemStack(Material.BOW))
        }

        items.add(PICKAXES.random())
        if (Random.nextFloat() < 0.4) {
            items.add(AXES.random())
        }

        if (Random.nextFloat() < 0.3) {
            items.add(SPADES.random())
        }

        if (Random.nextFloat() < 0.15) {
            items.add(ItemStack(Material.FISHING_ROD))
        }

        if (Random.nextFloat() < 0.3) {
            items.add(ItemStack(Material.GOLDEN_APPLE, VariableAmount.range(2.0, 5.0).flooredAmount))
        }

        if (Random.nextFloat() < 0.3) {
            items.add(ItemStack(Material.FLINT_AND_STEEL))

        }

        if (Random.nextFloat() < 0.4) {
            items.add(ItemStack(Material.TNT, VariableAmount.range(5.0, 15.0).flooredAmount))
        }

        if (Random.nextFloat() < 0.01) {
            items.add(
                ItemStackBuilder.of(Material.SLIME_BALL).name("&aСлизь Бога").enchant(Enchantment.KNOCKBACK, 3).build()
            )
        }

        if (Random.nextFloat() < 0.2) {
            items.add(ItemStack(Material.EGG, VariableAmount.range(2.0, 12.0).flooredAmount))
        }

        if (Random.nextFloat() < 0.2) {
            items.add(ItemStack(Material.SNOW_BALL, VariableAmount.range(2.0, 12.0).flooredAmount))
        }

        if (Random.nextFloat() < 0.3) {
            items.add(ItemStack(Material.ENCHANTMENT_TABLE))
            items.add(ItemStack(Material.EXP_BOTTLE, VariableAmount.range(12.0, 48.0).flooredAmount))

        }

        items.add(RandomSelector.uniform(FOOD).pick())
        items.add(ItemStack(Material.ARROW, VariableAmount.range(5.0, 40.0).flooredAmount))
        items.add(ItemStack(Material.WATER_BUCKET))
        items.add(ItemStack(Material.LAVA_BUCKET))
        when (Random.nextInt(4)) {
            in 0..2 -> {
                items.add(BLOCKS.random())
            }
            else -> {
                items.add(POTIONS.random().get())
                if (Random.nextFloat() < 0.1) {
                    items.add(POTIONS.random().get())

                }

            }


        }

        return items
    }

    private fun getWeightForType(type: Material): Double = when {
        isDiamond(type) -> {
            7.0
        }
        isGolden(type) -> {
            10.0

        }
        isIron(type) -> {
            20.0
        }
        isLeather(type) -> {
           10.0
        }
        else -> 20.0
    }


    override fun middle(): List<ItemStack> {
        TODO("Not yet implemented")
    }

    override fun mythic(): List<ItemStack> {
        TODO("Not yet implemented")
    }
}