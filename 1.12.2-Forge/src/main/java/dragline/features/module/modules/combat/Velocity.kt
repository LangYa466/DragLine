/*
 * 作者 Nelly QQ 3054086606
 * 改description死全家 改注释死全家 泄露死全家 反编译死全家
 */

package dragline.features.module.modules.combat

import dragline.DragLine
import dragline.api.minecraft.network.play.client.ICPacketUseEntity
import dragline.event.AttackEvent
import dragline.event.EventTarget
import dragline.event.PacketEvent
import dragline.event.UpdateEvent
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.utils.ClientUtils
import dragline.utils.extensions.getDistanceToEntityBox
import dragline.utils.timer.MSTimer
import dragline.value.BoolValue
import dragline.value.FloatValue
import dragline.value.ListValue
import nellyobfuscator.NellyClassObfuscator
import net.ccbluex.liquidbounce.injection.backend.unwrap
import net.minecraft.network.play.client.CPacketConfirmTransaction
import net.minecraft.network.play.client.CPacketEntityAction
import net.minecraft.network.play.server.SPacketEntityVelocity
import top.fl0wowp4rty.phantomshield.annotations.Native
import top.fl0wowp4rty.phantomshield.annotations.license.RegisterLock
import kotlin.math.cos
import kotlin.math.sin

@Native
@RegisterLock
@NellyClassObfuscator
@ModuleInfo(name = "Velocity", description = "by nelly", category = ModuleCategory.COMBAT)
class Velocity : Module() {

    // HytMotion = NOXZ Jumprester = Legit AttackReduce = "FakeLegit" Simpe = LiquidBounce Jump = LiquidBounce
    /**
     * OPTIONS
     */
    private val horizontalValue = FloatValue("Simple-Horizontal", 0F, 0F, 1F)
    private val verticalValue = FloatValue("Simple-Vertical", 0F, 0F, 1F)
    private val modeValue =
        ListValue("Mode", arrayOf("HytMotion", "JumpRester", "AttackReduce", "Simple", "Jump"), "Simple")

    val reduceAmount = FloatValue("AttackReduce-ReduceAmount", 0.8f, 0.3f, 1f)

    //HytMotion
    private val changesource = BoolValue("HytMotion-ChangeSource", false)
    private val noblock = BoolValue("HytMotion-Noblock", false)

    // test
    private val debug = BoolValue("MotionY-Debug", false)

    /**
     * VALUES
     */
    private var velocityTimer = MSTimer()
    private var velocityInput = false

    //sb cat code
    private var velocityInput2 = false

    //attack reduce
    var attack = false
    private var jumped = 0


    override val tag: String
        get() = modeValue.get()

    override fun onDisable() {
        mc.thePlayer?.speedInAir = 0.02F
    }

    @EventTarget
    fun onAttack(event: AttackEvent) {
        if (modeValue.get().equals("AttackReduce")) {

            if (mc.thePlayer!!.hurtTime < 3)
                return

            mc.thePlayer!!.motionX *= reduceAmount.get().toDouble()
            mc.thePlayer!!.motionZ *= reduceAmount.get().toDouble()
        }
    }


    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val thePlayer = mc.thePlayer ?: return

        if (thePlayer.isInWater || thePlayer.isInLava || thePlayer.isInWeb)
            return

        when (modeValue.get().toLowerCase()) {
            "jump" -> if (thePlayer.hurtTime > 0 && thePlayer.onGround) {
                thePlayer.motionY = 0.42

                val yaw = thePlayer.rotationYaw * 0.017453292F

                thePlayer.motionX -= sin(yaw) * 0.2
                thePlayer.motionZ += cos(yaw) * 0.2
            }

            "hytmotion" -> {
                if (velocityInput) {
                    if (attack) {
                        if (!changesource.get()) {
                            mc.thePlayer!!.motionX *= 0.077760000
                            mc.thePlayer!!.motionZ *= 0.077760000
                        }
                        velocityInput = false
                        attack = false
                    } else {
                        if (mc.thePlayer!!.hurtTime > 0) {
                            mc.thePlayer!!.motionX += -1.0E-7
                            mc.thePlayer!!.motionY += -1.0E-7
                            mc.thePlayer!!.motionZ += -1.0E-7
                            mc.thePlayer!!.isAirBorne = true
                        }
                        velocityInput = false
                        attack = false
                    }
                }

                if (velocityInput2) {
                    if (mc.thePlayer!!.onGround && mc.thePlayer!!.hurtTime == 9) {
                        if (jumped > 2) {
                            jumped = 0
                        } else {
                            ++jumped
                            if (mc.thePlayer!!.ticksExisted % 5 != 0) mc.gameSettings.keyBindJump.pressed = true
                        }
                    } else if (mc.thePlayer!!.hurtTime == 8) {
                        mc.gameSettings.keyBindJump.pressed = mc.gameSettings.isKeyDown(mc.gameSettings.keyBindJump)
                        velocityInput = false
                    }
                }
            }
        }

        // 测试啊老哥 你咋给注释取消了 他妈的我刚刚玩一直有消息我以为你写的什么垃圾东西炸了
        if (debug.get() && mc.thePlayer!!.hurtTime > 0) {
            ClientUtils.displayChatMessage("MotionY:${mc.thePlayer!!.motionY}")
        }

    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val thePlayer = mc.thePlayer ?: return

        val packet = event.packet
        val packet2 = event.packet.unwrap()

        if (classProvider.isSPacketEntityVelocity(packet)) {
            val packetEntityVelocity = packet.asSPacketEntityVelocity()


            if ((mc.theWorld?.getEntityByID(packetEntityVelocity.entityID) ?: return) != thePlayer)
                return

            velocityTimer.reset()

            when (modeValue.get().toLowerCase()) {
                "simple" -> {
                    val horizontal = horizontalValue.get()
                    val vertical = verticalValue.get()

                    if (horizontal == 0F && vertical == 0F)
                        event.cancelEvent()

                    packetEntityVelocity.motionX = (packetEntityVelocity.motionX * horizontal).toInt()
                    packetEntityVelocity.motionY = (packetEntityVelocity.motionY * vertical).toInt()
                    packetEntityVelocity.motionZ = (packetEntityVelocity.motionZ * horizontal).toInt()
                }

                "jumprester" -> {
                    if (thePlayer.hurtTime > 0 && thePlayer.onGround && packet2 is SPacketEntityVelocity) {
                        thePlayer.jump()
                    }
                }

                "hytmotion" -> {
                    velocityInput = true
                    velocityInput2 = true
                    if (mc.thePlayer!!.onGround) {
                        mc.gameSettings.keyBindJump.pressed = true
                    }
                    val aura = DragLine.moduleManager.getModule(KillAura::class.java) as KillAura
                    if (aura.state && aura.target != null && mc.thePlayer!!.getDistanceToEntityBox(aura.target!!) <= 3.01) {
                        //是否疾跑
                        if (mc.thePlayer!!.movementInput.moveForward > 0.9f && mc.thePlayer!!.sprinting && mc.thePlayer!!.serverSprintState) {
                            repeat(5) {
                                mc2.connection!!.sendPacket(CPacketConfirmTransaction(100, 100, true))
                                mc.thePlayer!!.sendQueue.addToSendQueue(
                                    classProvider.createCPacketUseEntity(
                                        aura.target!!,
                                        ICPacketUseEntity.WAction.ATTACK
                                    )
                                )
                                mc.thePlayer!!.sendQueue.addToSendQueue(classProvider.createCPacketAnimation())
                            }
                            if (thePlayer.isCollidedHorizontally && noblock.get() && !thePlayer.isOnLadder && !thePlayer.isInWater && !thePlayer.isInLava) return
                            if (changesource.get()) {
                                packetEntityVelocity.motionX = ((0.077760000 * 8000).toInt())
                                packetEntityVelocity.motionZ = ((0.077760000 * 8000).toInt())
                            }
                            attack = true
                        } else {
                            if (mc.thePlayer!!.movementInput.moveForward > 0.9f) {
                                repeat(5) {
                                    mc2.connection!!.sendPacket(CPacketConfirmTransaction(100, 100, true))
                                    mc2.connection!!.networkManager.sendPacket(
                                        CPacketEntityAction(
                                            mc2.player,
                                            CPacketEntityAction.Action.START_SPRINTING
                                        )
                                    )
                                    mc.thePlayer!!.sendQueue.addToSendQueue(
                                        classProvider.createCPacketUseEntity(
                                            aura.target!!,
                                            ICPacketUseEntity.WAction.ATTACK
                                        )
                                    )
                                    mc.thePlayer!!.sendQueue.addToSendQueue(classProvider.createCPacketAnimation())
                                }
                                if (thePlayer.isCollidedHorizontally && noblock.get() && !thePlayer.isOnLadder && !thePlayer.isInWater && !thePlayer.isInLava) return
                                if (changesource.get()) {
                                    packetEntityVelocity.motionX = ((0.077760000 * 8000).toInt())
                                    packetEntityVelocity.motionZ = ((0.077760000 * 8000).toInt())
                                }
                                attack = true
                            }
                        }
                    } else if (aura.state && aura.currentTarget != null && mc.thePlayer!!.getDistanceToEntityBox(aura.currentTarget!!) <= 3.01) {
                        //是否疾跑
                        if (mc.thePlayer!!.movementInput.moveForward > 0.9f && mc.thePlayer!!.sprinting && mc.thePlayer!!.serverSprintState) {
                            repeat(5) {
                                mc2.connection!!.sendPacket(CPacketConfirmTransaction(100, 100, true))
                                mc.thePlayer!!.sendQueue.addToSendQueue(
                                    classProvider.createCPacketUseEntity(
                                        aura.target!!,
                                        ICPacketUseEntity.WAction.ATTACK
                                    )
                                )
                                mc.thePlayer!!.sendQueue.addToSendQueue(classProvider.createCPacketAnimation())
                            }
                            if (thePlayer.isCollidedHorizontally && noblock.get() && !thePlayer.isOnLadder && !thePlayer.isInWater && !thePlayer.isInLava) return
                            if (changesource.get()) {
                                packetEntityVelocity.motionX = ((0.077760000 * 8000).toInt())
                                packetEntityVelocity.motionZ = ((0.077760000 * 8000).toInt())
                            }
                            attack = true
                        } else {
                            if (mc.thePlayer!!.movementInput.moveForward > 0.9f) {
                                repeat(5) {
                                    mc2.connection!!.sendPacket(CPacketConfirmTransaction(100, 100, true))
                                    mc2.connection!!.networkManager.sendPacket(
                                        CPacketEntityAction(
                                            mc2.player,
                                            CPacketEntityAction.Action.START_SPRINTING
                                        )
                                    )
                                    mc.thePlayer!!.sendQueue.addToSendQueue(
                                        classProvider.createCPacketUseEntity(
                                            aura.target!!,
                                            ICPacketUseEntity.WAction.ATTACK
                                        )
                                    )
                                    mc.thePlayer!!.sendQueue.addToSendQueue(classProvider.createCPacketAnimation())
                                }
                                if (thePlayer.isCollidedHorizontally && noblock.get() && !thePlayer.isOnLadder && !thePlayer.isInWater && !thePlayer.isInLava) return
                                if (changesource.get()) {
                                    packetEntityVelocity.motionX = ((0.077760000 * 8000).toInt())
                                    packetEntityVelocity.motionZ = ((0.077760000 * 8000).toInt())
                                }
                                attack = true
                            }
                        }
                    }
                }
            }
        }
    }
}

