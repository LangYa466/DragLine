/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.cape

import com.google.gson.JsonParser
import dragline.DragLine
import dragline.api.minecraft.client.render.WIImageBuffer
import dragline.api.minecraft.util.IResourceLocation
import dragline.utils.ClientUtils
import dragline.utils.MinecraftInstance
import me.nelly.utils.HttpUtils
import java.awt.image.BufferedImage
import java.util.*
import kotlin.collections.HashMap

object CapeAPI : MinecraftInstance() {

    // Cape Service
    private var capeService: CapeService? = null

    /**
     * Register cape service
     */
    fun registerCapeService() {
        // Read cape infos from web
        val jsonObject = JsonParser()
                .parse(HttpUtils.get("${DragLine.CLIENT_CLOUD}/capes.json")).asJsonObject
        val serviceType = jsonObject.get("serviceType").asString

        // Setup service type
        when (serviceType.toLowerCase()) {
            "api" -> {
                val url = jsonObject.get("api").asJsonObject.get("url").asString

                capeService = ServiceAPI(url)
                ClientUtils.getLogger().info("Registered $url as '$serviceType' service type.")
            }
            "list" -> {
                val users = HashMap<String, String>()

                for ((key, value) in jsonObject.get("users").asJsonObject.entrySet()) {
                    users[key] = value.asString
                    ClientUtils.getLogger().info("Loaded user cape for '$key'.")
                }

                capeService = ServiceList(users)
                ClientUtils.getLogger().info("Registered '$serviceType' service type.")
            }
        }

        ClientUtils.getLogger().info("Loaded.")
    }

    /**
     * Load cape of user with uuid
     *
     * @param uuid
     * @return cape info
     */
    fun loadCape(uuid: UUID): CapeInfo? {
        // Get url of cape from cape service
        val url = (capeService ?: return null).getCape(uuid) ?: return null

        // Load cape
        val resourceLocation = DragLine.wrapper.classProvider.createResourceLocation("capes/%s.png".format(uuid.toString()))
        val capeInfo = CapeInfo(resourceLocation)
        val threadDownloadImageData = DragLine.wrapper.classProvider.createThreadDownloadImageData(null, url, null, object :
            WIImageBuffer {

            override fun parseUserSkin(image: BufferedImage?): BufferedImage? {
                return image
            }

            override fun skinAvailable() {
                capeInfo.isCapeAvailable = true
            }

        })

        mc.textureManager.loadTexture(resourceLocation, threadDownloadImageData)

        return capeInfo
    }

    /**
     * Check if cape service is available
     *
     * @return capeservice status
     */
    fun hasCapeService() = capeService != null
}

data class CapeInfo(val resourceLocation: IResourceLocation, var isCapeAvailable: Boolean = false)