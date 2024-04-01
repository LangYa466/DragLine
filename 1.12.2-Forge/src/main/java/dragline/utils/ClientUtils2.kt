package dragline.utils

import com.google.gson.JsonObject
import dragline.DragLine
import dragline.ui.client.hud.element.elements.Notification
import dragline.ui.client.hud.element.elements.NotifyType

class ClientUtils2 {

    companion object {
        fun displayChatMessage(commandChatMode: Boolean, message: String) {
            if (MinecraftInstance.mc.thePlayer == null) {
                ClientUtils.getLogger().info("(MCChat) $message")
                return
            }

            val jsonObject = JsonObject()
            if (commandChatMode) {
                jsonObject.addProperty(
                    "text",
                    ("§b${DragLine.CLIENT_NAME} §7» " + message)
                )

                MinecraftInstance.mc.thePlayer!!.addChatMessage(
                    DragLine.wrapper.functions.jsonToComponent(
                        jsonObject.toString()
                    )
                )
            } else {
                jsonObject.addProperty("text", message)

                MinecraftInstance.mc.thePlayer!!.addChatMessage(
                    DragLine.wrapper.functions.jsonToComponent(
                        jsonObject.toString()
                    )
                )
            }
        }

        fun notificationsTransform(title: String, content: String, type: NotifyType) {
            DragLine.hud.addNotification(
                Notification(
                    title, content, type
                )
            )
        }
    }
}