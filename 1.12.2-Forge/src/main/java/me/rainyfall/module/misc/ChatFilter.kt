/*
 * ColorByte Hacked Client
 * A free half-open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/SkidderRyF/ColorByte/
 */

package me.rainyfall.module.misc

import dragline.event.EventTarget
import dragline.event.PacketEvent
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo

@ModuleInfo(name = "ChatFilter", description = "Ignore some useless messages.", category = ModuleCategory.MISC)
class ChatFilter : Module() {
    private val localWords = arrayOf(
        "世界BOSS等你来战！ >>>",
        "[世界]<系统> 尊贵的SVIP+玩家",
        "VIP]",
        "VIP+]",
        "抽奖中..",
        "正在进行匹配，请稍候",
        "=====================================================",
        "   发现可恨的作弊者？ >>>",
        "   请使用举报系统举报作弊者！ 花雨庭承诺查证一起，",
        "   处罚一起，绝不姑息任何作弊行为！ 举报请输入：",
        "     /report 玩家名",
        "   感谢各位玩家为花雨庭游戏环境一起做出的努力。",
        "=====================================================",
        "欢迎光临花雨庭 >>>",
        "冷却中!",
        "无法领取该奖励.",
        "花雨庭>> 操作太快了",
        "花雨庭>> 尚未拥有该时装",
        "已经超越神了！拜托谁去杀了他吧",
        "You are already connected",
        "You are already connecting",
        "Could not connect to a default or fallback server. Incorrectly configured address/port/firewall?",
        "你无法移动该物品",
        "起床战争 >> 请勿从缝隙里敲床",
        "起床战争 >> 无敌时间，无法攻击!",
        "起床战争 >> 该玩家正处于无敌时间!",
        "起床战争>> 你没有足够的资源购买这个物品!",
        "起床战争 >> 你没有足够的资源购买这个物品!",
        "你的 [经验] 不足无法购买该商品!",
        "你触发了双倍掉落物被动！",
        "搂c搂l您本日获得的 搂7搂l银币搂c搂l 已达上限,接下来的游戏将不再获得。",
        "该玩家处于无敌状态!"
    )
    private var blockMSG = 0

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (classProvider.isSPacketChat(packet)) {
            val chat = packet.asSPacketChat().chatComponent.unformattedText
            for (it in localWords) {
                if (chat.contains(it) || chat.equals(it) || chat.startsWith(it)) {
                    blockMSG++
                    event.cancelEvent()
                }
            }
        }
    }


    override val tag: String
        get() = "Blocked: $blockMSG"
}
