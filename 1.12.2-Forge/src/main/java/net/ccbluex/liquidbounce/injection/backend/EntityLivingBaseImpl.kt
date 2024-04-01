package net.ccbluex.liquidbounce.injection.backend

import dragline.api.minecraft.client.entity.IEntity
import dragline.api.minecraft.client.entity.IEntityLivingBase
import dragline.api.minecraft.entity.IEnumCreatureAttribute
import dragline.api.minecraft.item.IItemStack
import dragline.api.minecraft.potion.IPotion
import dragline.api.minecraft.potion.IPotionEffect
import dragline.api.minecraft.scoreboard.ITeam
import dragline.api.util.WrappedCollection
import net.ccbluex.liquidbounce.injection.backend.utils.toEntityEquipmentSlot
import net.minecraft.entity.EntityLivingBase
import net.minecraft.potion.Potion
import net.minecraft.potion.PotionEffect
import net.minecraft.util.EnumHand

open class EntityLivingBaseImpl<T : EntityLivingBase>(wrapped: T) : EntityImpl<T>(wrapped), IEntityLivingBase {
    override val maxHealth: Float
        get() = wrapped.maxHealth
    override var prevRotationYawHead: Float
        get() = wrapped.prevRotationYawHead
        set(value) {
            wrapped.prevRotationYawHead = value
        }
    override var prevRotationPitchHead: Float
        get() = wrapped.prevRotationPitch
        set(value) {
            wrapped.prevRotationPitch = value
        }
    override var renderYawOffset: Float
        get() = wrapped.renderYawOffset
        set(value) {
            wrapped.renderYawOffset = value
        }
    override val maxHurtTime: Int
        get() = wrapped.maxHurtTime
    override val activePotionEffects: Collection<IPotionEffect>
        get() = WrappedCollection<PotionEffect, IPotionEffect, Collection<PotionEffect>>(
            wrapped.activePotionEffects,
            IPotionEffect::unwrap,
            PotionEffect::wrap
        )
    override val isSwingInProgress: Boolean
        get() = wrapped.isSwingInProgress
    override var cameraPitch: Float
        get() = wrapped.cameraPitch
        set(value) {
            wrapped.cameraPitch = value
        }
    override val team: ITeam?
        get() = wrapped.team?.wrap()
    override val creatureAttribute: IEnumCreatureAttribute
        get() = wrapped.creatureAttribute.wrap()
    override val hurtTime: Int
        get() = wrapped.hurtTime
    override val maxhurtTime: Int
        get() = wrapped.maxHurtTime
    override val isOnLadder: Boolean
        get() = wrapped.isOnLadder
    override var jumpMovementFactor: Float
        get() = wrapped.jumpMovementFactor
        set(value) {
            wrapped.jumpMovementFactor = value
        }
    override val moveStrafing: Float
        get() = wrapped.moveStrafing
    override val moveForward: Float
        get() = wrapped.moveForward
    override var health: Float
        get() = wrapped.health
        set(value) {
            wrapped.health = value
        }
    override var rotationYawHead: Float
        get() = wrapped.rotationYawHead
        set(value) {
            wrapped.rotationYawHead = value
        }
    override val totalArmorValue: Int
        get() = wrapped.totalArmorValue

    override fun canEntityBeSeen(it: IEntity): Boolean = wrapped.canEntityBeSeen(it.unwrap())

    override fun isPotionActive(potion: IPotion): Boolean = wrapped.isPotionActive(potion.unwrap())

    override fun swingItem() = wrapped.swingArm(EnumHand.MAIN_HAND)

    override fun getActivePotionEffect(potion: IPotion): IPotionEffect? =
        wrapped.getActivePotionEffect(potion.unwrap())?.wrap()

    override fun removePotionEffectClient(id: Int) {
        wrapped.removeActivePotionEffect(Potion.getPotionById(id))
    }

    override fun addPotionEffect(effect: IPotionEffect) = wrapped.addPotionEffect(effect.unwrap())

    override fun getEquipmentInSlot(index: Int): IItemStack? =
        wrapped.getItemStackFromSlot(index.toEntityEquipmentSlot()).wrap()
}

inline fun IEntityLivingBase.unwrap(): EntityLivingBase = (this as EntityLivingBaseImpl<*>).wrapped
inline fun EntityLivingBase.wrap(): IEntityLivingBase = EntityLivingBaseImpl(this)