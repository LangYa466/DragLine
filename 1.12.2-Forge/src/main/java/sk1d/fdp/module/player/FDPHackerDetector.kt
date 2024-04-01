/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/SkidderMC/FDPClient/
 */
package sk1d.fdp.module.player

import dragline.DragLine
import dragline.event.EventTarget
import dragline.event.PacketEvent
import dragline.event.UpdateEvent
import dragline.event.WorldEvent
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.ui.client.hud.element.elements.Notification
import dragline.ui.client.hud.element.elements.NotifyType
import dragline.utils.timer.MSTimer
import dragline.utils.ClientUtils
import dragline.value.BoolValue
import dragline.value.IntegerValue
import net.minecraft.client.Minecraft
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.play.server.*
import net.minecraft.potion.Potion
import net.minecraft.util.math.AxisAlignedBB
import kotlin.math.*

@ModuleInfo(name = "FDPHackerDetector", description = "FDP", category = ModuleCategory.PLAYER)
class FDPHackerDetector : Module() {

    private val combatCheckValue = BoolValue("Combat", true)
    private val movementCheckValue = BoolValue("Movement", true)
    private val debugModeValue = BoolValue("Debug", false)
    private val notifyValue = BoolValue("Notify", true)
    private val vlValue = IntegerValue("VL", 300, 100, 500)

    private val hackerDataMap = HashMap<EntityPlayer, HackerData>()
    private val hackers = ArrayList<String>()

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        Thread { doGC() }.start()
    }

    private fun doGC() {
        val needRemove = ArrayList<EntityPlayer>()
        for ((player, _) in hackerDataMap) {
            if (player.isDead) {
                needRemove.add(player)
            }
        }
        for (player in needRemove) {
            hackerDataMap.remove(player)
            if (debugModeValue.get()) {
                ClientUtils.displayChatMessage("[GC] REMOVE ${player.name}")
            }
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (event.packet is SPacketEntityStatus) {
            val packet = event.packet
            if (combatCheckValue.get() && packet.opCode.toInt() == 2) {
                Thread { checkCombatHurt(packet.getEntity(mc2.world)) }.start()
            }
        } else if (event.packet is SPacketAnimation) {
            val packet = event.packet
            val entity = mc2.world.getEntityByID(packet.entityID)
            if (entity !is EntityPlayer || packet.animationType != 0) return
            val data = hackerDataMap[entity] ?: return
            data.tempAps++
        } else if (movementCheckValue.get()) {
            if (event.packet is SPacketEntityTeleport) {
                val packet = event.packet
                val entity = mc2.world.getEntityByID(packet.entityId)
                if (entity !is EntityPlayer) return
                Thread { checkPlayer(entity) }.start()
            } else if (event.packet is SPacketEntity) {
                val packet = event.packet
                val entity = packet.getEntity(mc2.world)
                if (entity !is EntityPlayer) return
                Thread { checkPlayer(entity) }.start()
            }
        }
    }

    override fun onEnable() {
        hackerDataMap.clear()
        hackers.clear()
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        hackerDataMap.clear()
    }

    fun isHacker(entity: EntityLivingBase): Boolean {
        if (entity !is EntityPlayer) return false
        return hackers.contains(entity.name)
    }

    private fun checkPlayer(player: EntityPlayer) {
        if (player == mc.thePlayer) return
        if (hackerDataMap[player] == null) hackerDataMap[player] = HackerData(player)
        val data = hackerDataMap[player] ?: return
        data.update()
        if (data.aliveTicks <20) return

        // settings
        var minAirTicks = 10
        if (player.isPotionActive(Potion.getPotionById(8))) { // 使用Potion.getPotionById()获取药水效果的ID
            minAirTicks += player.getActivePotionEffect(Potion.getPotionById(8))!!.amplifier * 3 // 使用Potion.getPotionById()获取药水效果
        }
        val maxMotionY = 0.47 // for strict check u can change this to 0.42
        val triggerBalance = 100
        val minimumClamp = 1000
        var passed = true

        // timer check
        val packetTimeNow = System.currentTimeMillis()
        var packetBalance = data.packetBalance
        val rate: Long = packetTimeNow - data.lastMovePacket
        packetBalance += 50.0
        packetBalance -= rate.toDouble()
        if (packetBalance >= triggerBalance) {
            val ticks = (packetBalance / 50).roundToInt()
            packetBalance = (-1 * (triggerBalance / 2)).toDouble()
            data.flag("timer", 25, "OVERSHOT TIMER $ticks")
        } else if (packetBalance < -1 * minimumClamp) {
            // Clamp minimum, 50ms=1tick of lag leniency
            packetBalance = (-1 * minimumClamp).toDouble()
        }
        if (packetBalance < triggerBalance) {
            data.packetBalance = packetBalance
            data.lastMovePacket = packetTimeNow
        }

        if (player.hurtTime> 0) {
            // velocity
            if (player.hurtResistantTime in 7..11 &&
                player.prevPosX == player.posX && player.posZ == player.lastTickPosZ &&
                !mc2.world.checkBlockCollision(player.entityBoundingBox.expand(0.05, 0.0, 0.05))) {
                data.flag("velocity", 50, "NO KNOCKBACK")
            }
            if (player.hurtResistantTime in 7..11 &&
                player.lastTickPosY == player.posY) {
                data.flag("velocity", 50, "NO KNOCKBACK")
            }
            return
        }

        // phase
//        if(mc.theWorld.checkBlockCollision(player.entityBoundingBox)){
//            flag("phase",50,data,"COLLIDE")
//            passed=false
//        }

        // killaura from jigsaw
        if (data.aps >= 10) {
            data.flag("killaura", 30, "HIGH APS(aps=${data.aps})")
            passed = false
        }
        if (data.aps > 2 && data.aps == data.preAps && data.aps != 0) {
            data.flag("killaura", 30, "STRANGE APS(aps=${data.aps})")
            passed = false
        }
        if (abs(player.rotationYaw - player.prevRotationYaw) > 50 && player.swingProgress != 0F &&
            data.aps >= 3) {
            data.flag("killaura", 30, "YAW RATE(aps=${data.aps},yawRot=${abs(player.rotationYaw - player.prevRotationYaw)})")
            passed = false
        }

        // flight
        if (player.ridingEntity == null && data.airTicks> (minAirTicks / 2)) {
            if (abs(data.motionY - data.lastMotionY) < (if (data.airTicks >= 115) { 1E-3 } else { 5E-3 })) {
                data.flag("fly", 20, "GLIDE(diff=${abs(data.motionY - data.lastMotionY)})")
                passed = false
            }
            if (data.motionY > maxMotionY) {
                data.flag("fly", 20, "YAXIS(motY=${data.motionY})")
                passed = false
            }
            if (data.airTicks > minAirTicks && data.motionY> 0) {
                data.flag("fly", 30, "YAXIS(motY=${data.motionY})")
                passed = false
            }
            // gravity check from ACR
//            val gravitatedY = (data.lastMotionY - 0.08) * GRAVITY_FRICTION
//            val offset = abs(gravitatedY - data.motionY)
//            if (offset > maxOffset) {
//                flag("fly",15,data,"GRAVITY(offset=$offset)")
//                passed=false
//            }
        }

        // speed
        val distanceXZ = abs(data.motionXZ)
        if (data.airTicks == 0) { // onGround
            var limit = 0.37
            if (data.groundTicks < 5) limit += 0.1
            if (player.isSneaking) limit *= 0.68
            if (player.isPotionActive(Potion.getPotionById(1))) { // 使用Potion.getPotionById()获取药水效果的ID
                limit += player.getActivePotionEffect(Potion.getPotionById(1))!!.amplifier // 使用Potion.getPotionById()获取药水效果
                limit *= 1.5
            }
            if (distanceXZ > limit) {
                data.flag("speed", 20, "GROUND SPEED(speed=$distanceXZ,limit=$limit)")
            }
        } else {
            val multiplier = 0.985
            var predict = 0.36 * multiplier.pow(data.airTicks + 1)
            if (data.airTicks >= 115) {
                predict = 0.08.coerceAtLeast(predict)
            }
            var limit = 0.05
            if (player.isPotionActive(Potion.getPotionById(1))) {
                predict += player.getActivePotionEffect(Potion.getPotionById(1))!!.amplifier * 0.05
                limit *= 1.2
            }

            if (player.isPotionActive(Potion.getPotionById(34))) {
                predict += player.getActivePotionEffect(Potion.getPotionById(34)!!)!!.amplifier * 0.05
            }



            if (distanceXZ - predict > limit) {
                data.flag("speed", 20, "AIR SPEED(speed=$distanceXZ,limit=$limit,predict=$predict)")
            }
        }
//        if (abs(data.motionX) > 0.42
//            || abs(data.motionZ) > 0.42){
//            flag("speed",30,data,"HIGH SPEED")
//            passed=false
//        }
//        if (player.isBlocking && (abs(data.motionX) > 0.2 || abs(data.motionZ) > 0.2)) {
//            flag("speed",30,data,"HIGH SPEED(BLOCKING)") //blocking is just noslow lol
//            passed=false
//        }

        // reduce vl
        if (passed) {
            data.vl -= 1
        }
    }

    private fun HackerData.flag(type: String, vl: Int, msg: String) {
        if (!this.useHacks.contains(type)) this.useHacks.add(type)
        // display debug message
        if (debugModeValue.get()) {
            ClientUtils.displayChatMessage("§f${this.player.name} §euse §2$type §7$msg §c${this.vl}+$vl")
        }
        this.vl += vl

        if (this.vl> vlValue.get()) {
            var use = ""
            for (typ in this.useHacks) {
                use += "$typ,"
            }
            use = use.substring(0, use.length - 1)
            ClientUtils.displayChatMessage("§f${this.player.name} §eusing hack §a$use")
            if (notifyValue.get()) {
                DragLine.hud.addNotification(Notification(name, "${this.player.name} might use hack ($use)", NotifyType.WARNING))
            }
            this.vl = -vlValue.get()


        }
    }

    private fun checkCombatHurt(entity: Entity) {
        if (entity !is EntityLivingBase) return
        var attacker: EntityPlayer? = null
        var attackerCount = 0

        for (worldEntity in mc2.world.loadedEntityList) {
            if (worldEntity !is EntityPlayer || worldEntity.getDistance(entity)> 7 || worldEntity.equals(entity)) continue
            attackerCount++
            attacker = worldEntity
        }

        // multi attacker may cause false result
        if (attackerCount != 1) return
        if (attacker!! == entity) return // i and my friend is hacker lol
        val data = hackerDataMap[attacker] ?: return

        // reach check
        val reach = attacker.getDistance(entity)
        if (reach> 3.7) {
            data.flag("killaura", 70, "(reach=$reach)")
        }

        // aim check
        val yawDiff = calculateYawDifference(attacker, entity)
        if (yawDiff> 50) {
            data.flag("killaura", 100, "(yawDiff=$yawDiff)")
        }
    }

    private fun calculateYawDifference(from: EntityLivingBase, to: EntityLivingBase): Double {
        val x = to.posX - from.posX
        val z = to.posZ - from.posZ
        return if (x == 0.0 && z == 0.0) {
            from.rotationYaw.toDouble()
        } else {
            val theta = atan2(-x, z)
            val yaw = Math.toDegrees((theta + 6.283185307179586) % 6.283185307179586)
            abs(180 - abs(abs(yaw - from.rotationYaw) - 180))
        }
    }
}

class HackerData(val player: EntityPlayer) {
    var packetBalance = 0.0
    var lastMovePacket = System.currentTimeMillis()
    var aliveTicks = 0
    // Ticks in air
    var airTicks = 0

    // Ticks on ground
    var groundTicks = 0

    // motion of the movement
    var motionX = 0.0
    var motionY = 0.0
    var motionZ = 0.0
    var motionXZ = 0.0

    // Previous motion of the movement
    private var lastMotionX = 0.0
    var lastMotionY = 0.0
    private var lastMotionZ = 0.0
    private var lastMotionXZ = 0.0

    // combat check
    var aps = 0
    var preAps = 0
    var tempAps = 0
    private val apsTimer = MSTimer()

    var vl = 0
    var useHacks = ArrayList<String>()

    fun update() {
        aliveTicks++
        if (apsTimer.hasTimePassed(1000)) {
            preAps = aps
            aps = tempAps
            tempAps = 0
        }

        if (calculateGround()) {
            groundTicks++
            airTicks = 0
        } else {
            airTicks++
            groundTicks = 0
        }

        this.lastMotionX = this.motionX
        this.lastMotionY = this.motionY
        this.lastMotionZ = this.motionZ
        this.lastMotionXZ = this.motionXZ
        this.motionX = player.posX - player.prevPosX
        this.motionY = player.posY - player.prevPosY
        this.motionZ = player.posZ - player.prevPosZ
        this.motionXZ = sqrt(motionX * motionX + motionZ * motionZ)
    }

    private fun calculateGround(): Boolean {
        val playerBoundingBox = player.entityBoundingBox
        val blockHeight = 1
        val customBox = AxisAlignedBB(playerBoundingBox.maxX, player.posY - blockHeight, playerBoundingBox.maxZ, playerBoundingBox.minX, player.posY, playerBoundingBox.minZ)
        return Minecraft.getMinecraft().world.checkBlockCollision(customBox)
    }
}