package dragline.utils

import dragline.DragLine
import dragline.api.enums.WEnumHand
import dragline.api.minecraft.item.IItemStack
import dragline.api.minecraft.network.IPacket
import dragline.api.minecraft.network.play.client.ICPacketClientStatus
import dragline.api.minecraft.network.play.client.ICPacketEntityAction
import net.ccbluex.liquidbounce.injection.backend.Backend
import net.ccbluex.liquidbounce.injection.backend.WrapperImpl.classProvider

fun createUseItemPacket(itemStack: IItemStack?, hand: WEnumHand): IPacket {
    @Suppress("ConstantConditionIf")
    return if (Backend.MINECRAFT_VERSION_MINOR == 8) {
        classProvider.createCPacketPlayerBlockPlacement(itemStack)
    } else {
        classProvider.createCPacketTryUseItem(hand)
    }
}

fun createUseItemPacket(hand: WEnumHand): IPacket {
    return classProvider.createCPacketTryUseItem(hand)
}

fun createOpenInventoryPacket(): IPacket {
    @Suppress("ConstantConditionIf")
    return if (Backend.MINECRAFT_VERSION_MINOR == 8) {
        classProvider.createCPacketClientStatus(ICPacketClientStatus.WEnumState.OPEN_INVENTORY_ACHIEVEMENT)
    } else {
        classProvider.createCPacketEntityAction(DragLine.wrapper.minecraft.thePlayer!!, ICPacketEntityAction.WAction.OPEN_INVENTORY)
    }
}
