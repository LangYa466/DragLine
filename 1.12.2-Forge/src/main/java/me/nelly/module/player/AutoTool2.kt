package me.nelly.module.player

import dragline.DragLine
import dragline.api.minecraft.item.IItemStack
import dragline.event.MotionEvent
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.features.module.modules.combat.KillAura
import dragline.value.BoolValue
import net.minecraft.block.Block
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemSword


@ModuleInfo("AutoTool2", "switches to the best tool" , ModuleCategory.PLAYER)
class AutoTool2 : Module() {
    private val autoSword: BoolValue = BoolValue("AutoSword", true)


    fun onMotion(event: MotionEvent) {
       val ka = DragLine.moduleManager.getModule(KillAura::class.java) as KillAura
        if (mc2.objectMouseOver != null && mc2.gameSettings.keyBindAttack.isKeyDown) {
            val objectMouseOver = mc2.objectMouseOver
            if (objectMouseOver.entityHit != null) {
                switchSword()
            } else {
                val block: Block = mc2.world!!.getBlockState(objectMouseOver.getBlockPos()).block
                updateItem(block)
            }
        } else if (ka.target != null) {
            switchSword()
        }
    }

    private fun updateItem(block: Block) {
        var strength = 1.0f
        var bestItem = -1
        for (i in 0..8) {
            val itemStack: IItemStack? = mc.thePlayer!!.inventory.mainInventory.get(i)
            val strVsBlock: Float = itemStack!!.getStrVsBlock(block)
            if (strVsBlock > strength) {
                strength = strVsBlock
                bestItem = i
            }
        }
        if (bestItem != -1) {
            mc2.player!!.inventory.currentItem = bestItem
        }
    }

    private fun switchSword() {
        if (!autoSword.get()) return
        var damage = 1f
        var bestItem = -1
        for (i in 0..8) {
            val `is`: ItemStack = mc2.player.inventory.mainInventory.get(i)
            if (`is`.item is ItemSword && getSwordStrength(`is`) > damage) {
                damage = getSwordStrength(`is`)
                bestItem = i
            }
        }
        if (bestItem != -1) {
            mc2.player.inventory.currentItem = bestItem
        }
    }

    private fun getSwordStrength(stack: ItemStack): Float {
        if (stack.item is ItemSword) {
            val sword = stack.item as ItemSword
            val sharpness = getEnchantmentLevel(stack, "minecraft:sharpness")
            val fireAspect = getEnchantmentLevel(stack, "minecraft:fire_aspect")
            return sword.attackDamage + sharpness * 1.25f + fireAspect * 1.5f
        }
        return 0f
    }

    private fun getEnchantmentLevel(stack: ItemStack, enchantmentName: String): Int {
        val nbtList = stack.enchantmentTagList
        for (i in 0 until nbtList.tagCount()) {
            val nbt = nbtList.getCompoundTagAt(i)
            val id = nbt.getString("id")
            val level = nbt.getShort("lvl")
            if (id == enchantmentName) {
                return level.toInt()
            }
        }
        return 0
    }

}
