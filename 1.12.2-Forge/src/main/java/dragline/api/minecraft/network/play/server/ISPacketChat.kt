package dragline.api.minecraft.network.play.server

import dragline.api.minecraft.network.IPacket
import net.minecraft.util.text.ChatType
import net.minecraft.util.text.ITextComponent

interface ISPacketChat : IPacket {

    val chatComponent: ITextComponent
    val type:ChatType
    val getChat :ITextComponent





}