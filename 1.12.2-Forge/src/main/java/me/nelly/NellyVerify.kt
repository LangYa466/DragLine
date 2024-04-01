package me.nelly

import dragline.utils.ClientUtils
import me.nelly.utils.HttpUtils
import nellyobfuscator.NellyClassObfuscator
import dragline.utils.MinecraftInstance
import top.fl0wowp4rty.phantomshield.annotations.Native
import top.fl0wowp4rty.phantomshield.annotations.license.RegisterLock
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
@Native
@RegisterLock
@NellyClassObfuscator
class NellyVerify : MinecraftInstance() {
    companion object {
        @JvmStatic
        fun verify() {
            if(HttpUtils.get("").contains(getHWID())) {
                 ClientUtils.getLogger().info("ok")
            } else {
                ClientUtils.getLogger().error("ERROR")
                mc.shutdown()
                mc2.shutdown()
                System.exit(0)
            }
        }
        private fun getHWID(): String {
            return try {
                val s = StringBuilder()
                val main = System.getenv("PROCESS_IDENTIFIER") + System.getenv("COMPUTERNAME")
                val bytes = main.toByteArray(StandardCharsets.UTF_8)
                val messageDigest = MessageDigest.getInstance("SHA")
                val sha = messageDigest.digest(bytes)
                var i = 0
                for (b in sha) {
                    s.append(Integer.toHexString(b.toInt() and 0xFF or 0x300), 0, 3)
                    if (i != sha.size - 1) {
                        s.append("-")
                    }
                    i++
                }
                encode(s.toString())
            } catch (e: NoSuchAlgorithmException) {
                throw RuntimeException(e)
            }
        }
        fun encode(text: String): String {
            try {
                //获取md5加密对象
                val instance: MessageDigest = MessageDigest.getInstance("MD5")
                //对字符串加密，返回字节数组
                val digest:ByteArray = instance.digest(text.toByteArray())
                val sb : StringBuffer = StringBuffer()
                for (b in digest) {
                    //获取低八位有效值
                    val i :Int = b.toInt() and 0xff
                    //将整数转化为16进制
                    var hexString = Integer.toHexString(i)
                    if (hexString.length < 2) {
                        //如果是一位的话，补0
                        hexString = "0$hexString"
                    }
                    sb.append(hexString)
                }
                return sb.toString()

            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            }

            return ""
        }
    }
}