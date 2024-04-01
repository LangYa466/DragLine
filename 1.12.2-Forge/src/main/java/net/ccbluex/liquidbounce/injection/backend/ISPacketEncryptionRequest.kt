package net.ccbluex.liquidbounce.injection.backend

import dragline.api.minecraft.network.login.server.ISPacketEncryptionRequest
import net.minecraft.network.login.server.SPacketEncryptionRequest

class SPacketEncryptionRequestImpl<T : SPacketEncryptionRequest>(wrapped: T) : PacketImpl<T>(wrapped),
    ISPacketEncryptionRequest {
    override val verifyToken: ByteArray
        get() = wrapped.verifyToken

}

inline fun ISPacketEncryptionRequest.unwrap(): SPacketEncryptionRequest = (this as SPacketEncryptionRequestImpl<*>).wrapped
inline fun SPacketEncryptionRequest.wrap(): ISPacketEncryptionRequest = SPacketEncryptionRequestImpl(this)