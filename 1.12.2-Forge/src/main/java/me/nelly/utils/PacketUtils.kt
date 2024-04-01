package me.nelly.utils

import dragline.utils.MinecraftInstance
import net.minecraft.network.Packet

object PacketUtils : MinecraftInstance() {

    // 发送数据包
    fun send(packet : Packet<*>) {
        mc2.connection!!.networkManager.sendPacket(packet)
    }
}