/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.utils.extensions

import dragline.api.minecraft.client.entity.IEntity
import dragline.api.minecraft.client.entity.IEntityLivingBase
import dragline.api.minecraft.client.entity.player.IEntityPlayer
import dragline.api.minecraft.util.IAxisAlignedBB
import dragline.api.minecraft.util.WVec3
import dragline.utils.render.ColorUtils.stripColor
import net.minecraft.client.Minecraft
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Allows to get the distance between the current entity and [entity] from the nearest corner of the bounding box
 */
fun IEntity.getDistanceToEntityBox(entity: IEntity): Double {
    val eyes = this.getPositionEyes(1F)
    val pos = getNearestPointBB(eyes, entity.entityBoundingBox)
    val xDist = abs(pos.xCoord - eyes.xCoord)
    val yDist = abs(pos.yCoord - eyes.yCoord)
    val zDist = abs(pos.zCoord - eyes.zCoord)
    return sqrt(xDist.pow(2) + yDist.pow(2) + zDist.pow(2))
}

fun getNearestPointBB(eye: WVec3, box: IAxisAlignedBB): WVec3 {
    val origin = doubleArrayOf(eye.xCoord, eye.yCoord, eye.zCoord)
    val destMins = doubleArrayOf(box.minX, box.minY, box.minZ)
    val destMaxs = doubleArrayOf(box.maxX, box.maxY, box.maxZ)
    for (i in 0..2) {
        if (origin[i] > destMaxs[i]) origin[i] = destMaxs[i] else if (origin[i] < destMins[i]) origin[i] = destMins[i]
    }
    return WVec3(origin[0], origin[1], origin[2])
}

fun IEntity.isAnimal(): Boolean {
    return dragline.DragLine.wrapper.classProvider.isEntityAnimal(this) ||
            dragline.DragLine.wrapper.classProvider.isEntitySquid(this) ||
            dragline.DragLine.wrapper.classProvider.isEntityGolem(this) ||
            dragline.DragLine.wrapper.classProvider.isEntityBat(this)
}

fun IEntity.isMob(): Boolean {
    return dragline.DragLine.wrapper.classProvider.isEntityMob(this) ||
            dragline.DragLine.wrapper.classProvider.isEntityVillager(this) ||
            dragline.DragLine.wrapper.classProvider.isEntitySlime(this)
            || dragline.DragLine.wrapper.classProvider.isEntityGhast(this) ||
            dragline.DragLine.wrapper.classProvider.isEntityDragon(this) ||
            dragline.DragLine.wrapper.classProvider.isEntityShulker(this)
}

fun IEntityPlayer.isClientFriend(): Boolean {
    val entityName = name ?: return false

    return dragline.DragLine.fileManager.friendsConfig.isFriend(stripColor(entityName))
}

val IEntityLivingBase.renderHurtTime: Float
    get() = this.hurtTime - if (this.hurtTime != 0) {
        Minecraft.getMinecraft().timer.renderPartialTicks
    } else {
        0f
    }
val IEntityLivingBase.hurtPercent: Float
    get() = (this.renderHurtTime) / 10
