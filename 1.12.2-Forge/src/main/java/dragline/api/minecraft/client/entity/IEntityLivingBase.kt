package dragline.api.minecraft.client.entity

import dragline.api.minecraft.entity.IEnumCreatureAttribute
import dragline.api.minecraft.item.IItemStack
import dragline.api.minecraft.potion.IPotion
import dragline.api.minecraft.potion.IPotionEffect
import dragline.api.minecraft.scoreboard.ITeam

interface IEntityLivingBase : IEntity {
    val maxHealth: Float
    var prevRotationYawHead: Float
    var prevRotationPitchHead: Float

    var renderYawOffset: Float
    val activePotionEffects: Collection<IPotionEffect>
    val isSwingInProgress: Boolean
    var cameraPitch: Float
    val team: ITeam?
    val creatureAttribute: IEnumCreatureAttribute
    val hurtTime: Int
    val maxhurtTime: Int
    val isOnLadder: Boolean
    var jumpMovementFactor: Float
    val moveStrafing: Float
    val maxHurtTime: Int
    val moveForward: Float
    var health: Float
    var rotationYawHead: Float
    val totalArmorValue: Int

    fun canEntityBeSeen(it: IEntity): Boolean
    fun isPotionActive(potion: IPotion): Boolean
    fun swingItem()
    fun getActivePotionEffect(potion: IPotion): IPotionEffect?
    fun removePotionEffectClient(id: Int)
    fun addPotionEffect(effect: IPotionEffect)
    fun getEquipmentInSlot(index: Int): IItemStack?
}