package me.paimon.utils

import dragline.DragLine

object IconManager {
    @JvmField
    val removeIcon = DragLine.wrapper.classProvider.createResourceLocation("liquidbounce/clickgui/error.png")
    val add = DragLine.wrapper.classProvider.createResourceLocation("liquidbounce/clickgui/import.png")
    val back = DragLine.wrapper.classProvider.createResourceLocation("liquidbounce/clickgui/back.png")
    val docs = DragLine.wrapper.classProvider.createResourceLocation("liquidbounce/clickgui/docs.png")
    val download = DragLine.wrapper.classProvider.createResourceLocation("liquidbounce/clickgui/download.png")
    val folder = DragLine.wrapper.classProvider.createResourceLocation("liquidbounce/clickgui/folder.png")
    val online = DragLine.wrapper.classProvider.createResourceLocation("liquidbounce/clickgui/online.png")
    val reload = DragLine.wrapper.classProvider.createResourceLocation("liquidbounce/clickgui/reload.png")
    val search = DragLine.wrapper.classProvider.createResourceLocation("liquidbounce/clickgui/search.png")
}