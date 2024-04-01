/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.features.module.modules.movement

import dragline.DragLine
import dragline.api.enums.WEnumHand
import dragline.api.minecraft.item.IItem
import dragline.api.minecraft.item.IItemStack
import dragline.api.minecraft.network.play.client.*
import dragline.api.minecraft.util.IEnumFacing
import dragline.api.minecraft.util.WBlockPos
import dragline.event.*
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.features.module.modules.combat.KillAura
import dragline.utils.MovementUtils
import dragline.utils.createUseItemPacket
import dragline.utils.timer.MSTimer
import dragline.value.BoolValue
import dragline.value.FloatValue
import dragline.value.IntegerValue
import dragline.value.ListValue
import me.nelly.utils.PacketUtils
import nellyobfuscator.NellyClassObfuscator
import net.ccbluex.liquidbounce.injection.backend.unwrap
import net.minecraft.item.*
import net.minecraft.network.Packet
import net.minecraft.network.play.INetHandlerPlayServer
import net.minecraft.network.play.client.*
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import top.fl0wowp4rty.phantomshield.annotations.Native
import top.fl0wowp4rty.phantomshield.annotations.license.RegisterLock
import java.util.*

@Native
@RegisterLock
@NellyClassObfuscator
@ModuleInfo(name = "NoSlow", description = "Cancels slowness effects caused by SoulSand and using items.", category = ModuleCategory.MOVEMENT)
class NoSlow : Module() {

    private val modeValue = ListValue(
        "PacketMode", arrayOf(
            "None",
            "GrimAC",
            "Vanilla",
            "NoPacket",
            "AAC",
            "AAC5",
            "Matrix",
            "Vulcan",
            "Custom",
            "Intave"
        ), "GrimAC"
    )

    private val hytabValue = BoolValue("HYtAutoBlockPacket",true)
    private val blockForwardMultiplier = FloatValue("BlockForwardMultiplier", 1.0F, 0.2F, 1.0F)
    private val blockStrafeMultiplier = FloatValue("BlockStrafeMultiplier", 1.0F, 0.2F, 1.0F)
    private val consumeForwardMultiplier = FloatValue("ConsumeForwardMultiplier", 1.0F, 0.2F, 1.0F)
    private val consumeStrafeMultiplier = FloatValue("ConsumeStrafeMultiplier", 1.0F, 0.2F, 1.0F)
    private val bowForwardMultiplier = FloatValue("BowForwardMultiplier", 1.0F, 0.2F, 1.0F)
    private val bowStrafeMultiplier = FloatValue("BowStrafeMultiplier", 1.0F, 0.2F, 1.0F)
    private val customOnGround =
        BoolValue("CustomOnGround", false).displayable { modeValue.get().contains("Custom".trim()) }
    private val customDelayValue =
        IntegerValue("CustomDelay", 60, 10, 200).displayable { modeValue.get().contains("Custom".trim()) }

    // Soulsand
    val soulsandValue = BoolValue("Soulsand", true)

    val timer = MSTimer()
    private val Timer = MSTimer()
    private var pendingFlagApplyPacket = false
    private val msTimer = MSTimer()
    private var sendBuf = false
    private var packetBuf = LinkedList<Packet<INetHandlerPlayServer>>()
    private var nextTemp = false
    private var waitC03 = false
    private var lastBlockingStat = false

    private val killAura = DragLine.moduleManager[KillAura::class.java] as KillAura


    private fun isBlock(): Boolean {
        return mc.thePlayer!!.isBlocking || killAura.blockingStatus
    }

    private fun onPre(event: MotionEvent): Boolean {
        return event.eventState == EventState.PRE
    }

    private fun onPost(event: MotionEvent): Boolean {
        return event.eventState == EventState.POST
    }

    private val isBlocking: Boolean
        get() = (mc.thePlayer!!.isUsingItem || (DragLine.moduleManager[KillAura::class.java] as KillAura).blockingStatus) && mc.thePlayer!!.heldItem != null && mc.thePlayer!!.heldItem!!.item is ItemSword

    private val isEatting: Boolean
        get() = mc.thePlayer!!.isUsingItem && mc.thePlayer!!.heldItem != null && (mc.thePlayer!!.heldItem!!.item!!.unwrap() is ItemFood) || (mc.thePlayer!!.heldItem!!.item!!.unwrap() is ItemPotion)

    private val isBowing: Boolean
        get() = mc.thePlayer!!.isUsingItem && mc.thePlayer!!.heldItem != null && (mc.thePlayer!!.heldItem!!.item!!.unwrap() is ItemBow) || (mc.thePlayer!!.heldItem!!.item!!.unwrap() is ItemBow)

    override fun onDisable() {
        Timer.reset()
        msTimer.reset()
        pendingFlagApplyPacket = false
        sendBuf = false
        packetBuf.clear()
        nextTemp = false
        waitC03 = false
    }

    private fun isHoldingPotionAndSword(stack: ItemStack?, checkSword: Boolean, checkPotionFood: Boolean): Boolean {
        return if (stack == null) {
            false
        } else if (stack.item is ItemAppleGold && checkPotionFood) {
            true
        } else if (stack.item is ItemFood && checkPotionFood) {
            true
        } else if (stack.item is ItemSword && checkSword) {
            true
        } else if (stack.item is ItemBow) {
            false
        } else {
            stack.item is ItemBucketMilk && checkPotionFood
        }
    }

    private fun sendPacket(
        Event: MotionEvent,
        SendC07: Boolean,
        SendC08: Boolean,
        Delay: Boolean,
        DelayValue: Long,
        onGround: Boolean,
        Hypixel: Boolean = false
    ) {
        val digging = classProvider.createCPacketPlayerDigging(
            ICPacketPlayerDigging.WAction.RELEASE_USE_ITEM,
            WBlockPos(-1, -1, -1),
            EnumFacing.DOWN as IEnumFacing
        )
        val blockPlace =
            classProvider.createCPacketPlayerBlockPlacement(mc.thePlayer!!.inventory.currentItem as IItemStack)
        val blockMent = classProvider.createCPacketPlayerBlockPlacement(
            WBlockPos(-1, -1, -1),
            255,
            mc.thePlayer!!.inventory.currentItem as IItemStack,
            0f,
            0f,
            0f
        )
        if (onGround && !mc.thePlayer!!.onGround) {
            return
        }

        if (SendC07 && onPre(Event)) {
            if (Delay && Timer.hasTimePassed(DelayValue)) {
                mc.netHandler.addToSendQueue(digging)
            } else if (!Delay) {
                mc.netHandler.addToSendQueue(digging)
            }
        }
        if (SendC08 && onPost(Event)) {
            if (Delay && Timer.hasTimePassed(DelayValue) && !Hypixel) {
                mc.netHandler.addToSendQueue(blockPlace)
                Timer.reset()
            } else if (!Delay && !Hypixel) {
                mc.netHandler.addToSendQueue(blockPlace)
            } else if (Hypixel) {
                mc.netHandler.addToSendQueue(blockMent)
            }
        }
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        mc.thePlayer ?: return

        if (!MovementUtils.isMoving) {
            return
        }

        if(hytabValue.get() && isBlocking && !isEatting && !isBlocking && !mc.thePlayer!!.sneaking) {
            PacketUtils.send(CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, BlockPos(mc.thePlayer!!.posX, mc.thePlayer!!.posY, mc.thePlayer!!.posZ), EnumFacing.DOWN))
        }

        when (modeValue.get().toLowerCase()) {
            "grimac" -> {
                if (event.eventState == EventState.PRE && (mc.thePlayer!!.isUsingItem || isBlocking) && this.isHoldingPotionAndSword(
                        mc2.player.getHeldItem(EnumHand.MAIN_HAND),
                        true,
                        false
                    )
                ) {
                    mc2.connection!!.sendPacket(CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN))
                }

                if (event.eventState == EventState.POST && (mc.thePlayer!!.isUsingItem || isBlocking) && this.isHoldingPotionAndSword(
                        mc2.player.getHeldItem(EnumHand.MAIN_HAND),
                        true,
                        false
                    )
                ) {
                    PacketUtils.send(CPacketConfirmTransaction(Int.MAX_VALUE, 32767.toShort(), true))
                    mc.playerController.sendUseItem(mc.thePlayer!!, mc.theWorld!!, mc.thePlayer!!.heldItem!!)
                }
            }
            "custom" -> {
                sendPacket(event, true, true, true, customDelayValue.get().toLong(), customOnGround.get())
            }

            "vanilla" -> {
                mc.thePlayer!!.motionX = mc.thePlayer!!.motionX
                mc.thePlayer!!.motionY = mc.thePlayer!!.motionY
                mc.thePlayer!!.motionZ = mc.thePlayer!!.motionZ
            }


            "aac" -> {
                if (mc.thePlayer!!.ticksExisted % 3 == 0) {
                    sendPacket(event, true, false, false, 0, false)
                } else {
                    sendPacket(event, false, true, false, 0, false)
                }
            }

            "aac5" -> {
                if (mc.thePlayer!!.isUsingItem || mc.thePlayer!!.isBlocking || isBlock()) {
                    mc.netHandler.addToSendQueue(
                        createUseItemPacket(
                            mc.thePlayer!!.inventory.getCurrentItemInHand(),
                            WEnumHand.MAIN_HAND
                        )
                    )
                    mc.netHandler.addToSendQueue(
                        createUseItemPacket(
                            mc.thePlayer!!.inventory.getCurrentItemInHand(),
                            WEnumHand.OFF_HAND
                        )
                    )
                }
            }
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet.unwrap()
        if (modeValue.equals("Intave")) {
            if (packet is CPacketAnimation) {
                event.cancelEvent()
            }
            consumeForwardMultiplier.set(1.0)
            consumeStrafeMultiplier.set(1.0)
        }
        if (modeValue.equals("Matrix") || modeValue.equals("Vulcan") && nextTemp) {
            if ((packet is CPacketPlayerDigging || packet is ICPacketPlayerBlockPlacement) && isBlocking) {
                event.cancelEvent()
            }
            event.cancelEvent()
        } else if (packet is CPacketPlayer || packet is CPacketAnimation || packet is CPacketEntityAction || packet is CPacketUseEntity || packet is CPacketPlayerDigging || packet is ICPacketPlayerBlockPlacement) {
            if (modeValue.equals("Vulcan") && waitC03 && packet is ICPacketPlayer) {
                waitC03 = false
                return
            }
            packetBuf.add(packet as Packet<INetHandlerPlayServer>)
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if ((modeValue.equals("Matrix") || modeValue.equals("Vulcan")) && (lastBlockingStat || isBlocking)) {
            if (msTimer.hasTimePassed(230) && nextTemp) {
                nextTemp = false
                classProvider.createCPacketPlayerDigging(
                    ICPacketPlayerDigging.WAction.RELEASE_USE_ITEM,
                    WBlockPos(-1, -1, -1),
                    EnumFacing.DOWN as IEnumFacing
                )
                if (packetBuf.isNotEmpty()) {
                    var canAttack = false
                    for (packet in packetBuf) {
                        if (packet is CPacketPlayer) {
                            canAttack = true
                        }
                        if (!((packet is ICPacketUseEntity || packet is ICPacketAnimation) && !canAttack)) {
                            mc2.connection!!.networkManager.sendPacket(packet)
                        }
                    }
                    packetBuf.clear()
                }
            }
        }
    }

    @EventTarget
    fun onSlowDown(event: SlowDownEvent) {
        val heldItem = mc.thePlayer!!.heldItem?.item

        event.forward = getMultiplier(heldItem, true)
        event.strafe = getMultiplier(heldItem, false)
    }

    private fun getMultiplier(item: IItem?, isForward: Boolean): Float {
        return when {
            classProvider.isItemFood(item) || classProvider.isItemPotion(item) || classProvider.isItemBucketMilk(item) -> {
                if (isForward) this.consumeForwardMultiplier.get() else this.consumeStrafeMultiplier.get()
            }

            classProvider.isItemSword(item) -> {
                if (isForward) this.blockForwardMultiplier.get() else this.blockStrafeMultiplier.get()
            }

            classProvider.isItemBow(item) -> {
                if (isForward) this.bowForwardMultiplier.get() else this.bowStrafeMultiplier.get()
            }

            else -> 0.2F
        }
    }

    override val tag: String
        get() = modeValue.get()
}
