/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package net.ccbluex.liquidbounce.injection.backend

import dragline.api.minecraft.nbt.IJsonToNBT
import dragline.api.minecraft.nbt.INBTTagCompound
import net.minecraft.nbt.JsonToNBT

object JsonToNBTImpl : IJsonToNBT {
    override fun getTagFromJson(s: String): INBTTagCompound = JsonToNBT.getTagFromJson(s).wrap()
}