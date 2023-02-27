package me.kqn

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.Plugin
import taboolib.common.platform.command.PermissionDefault
import taboolib.common.platform.command.command
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.info
import taboolib.common.platform.function.releaseResourceFile
import taboolib.common.util.asList
import taboolib.common5.FileWatcher
import taboolib.expansion.createHelper
import taboolib.module.chat.colored
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import taboolib.module.kether.KetherShell
import taboolib.platform.compat.replacePlaceholder
import java.io.File

object RandomCommand : Plugin() {
    lateinit var config:Configuration
    private fun read(){
        releaseResourceFile("config.yml",false)
        config= Configuration.loadFromFile(File("plugins/RandomCommand/config.yml"),Type.YAML)
    }
    override fun onEnable() {
        read()
        FileWatcher.INSTANCE.addSimpleListener(File("plugins/RandomCommand/config.yml")){
            read()
        }
        command("RandomCommand", permissionDefault = PermissionDefault.TRUE){
            createHelper(true)
            dynamic(permission="RandomCommand.scripts") {
                dynamic (permission="RandomCommand.player"){
                    execute<CommandSender>{
                        sender, context, argument ->
                        val scripts= config.getStringList(context.argument(-1))
                        val player=Bukkit.getPlayerExact(context.argument(0))
                        if(player==null){
                            sender.sendMessage("&cÍæ¼Ò${context.argument(0)}²»´æÔÚ")
                            return@execute
                        }
                        for (script in scripts) {
                            script.eval(player)
                        }
                    }
                }
            }
        }
    }
    fun String.eval(player:Player){
        try {
            KetherShell.eval(this, sender = adaptPlayer(player))
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}