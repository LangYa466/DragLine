package dragline.api.minecraft.network.login.server

interface ISPacketEncryptionRequest {
    val verifyToken: ByteArray
}