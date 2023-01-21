package me.kqn.TeamTp

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.Plugin
import taboolib.common.platform.command.PermissionDefault
import taboolib.common.platform.command.command
import taboolib.common.platform.function.adaptLocation
import taboolib.common.platform.function.info
import taboolib.common.platform.function.submitAsync
import taboolib.module.chat.colored
import taboolib.module.configuration.util.setLocation
import taboolib.platform.util.ItemBuilder
import taboolib.platform.util.giveItem
import taboolib.platform.util.inputBook
import java.util.*
import kotlin.collections.HashMap

object TeamTp : Plugin() {
    lateinit var item:ItemStack

    var setArea=HashMap<UUID,Pair<Location?,Location?>>()
    override fun onEnable() {
        info("Successfully running ExamplePlugin!")
        item=ItemBuilder(Material.STICK).also { it.name="&f区域选取工具".colored()
        it.lore.add("&7左键点击-设置第一角点".colored())
            it.lore.add("&7右键点击-设置第二角点".colored())
            it.lore.add("&7设置完毕后输入/tmtp save 保存区域".colored())
        }.build()
        regcmd()
    }

    private fun regcmd(){
        command(name="TeamTp", aliases = listOf("tmtp"), permission = "TeamTp.use", permissionDefault = PermissionDefault.OP){
            execute<Player>{sender, context, argument ->
                sender.sendMessage("&a/tmtp tool 获取设置区域的工具".colored())
                sender.sendMessage("&a/tmtp save <区域名>  保存区域".colored())
                sender.sendMessage("&a/tmtp setAction <区域名> 为指定区域设置脚本".colored())


            }
            literal("tool"){
                execute<Player>{sender, context, argument ->
                    sender.giveItem(item.clone())
                }
            }
            literal("save"){
                dynamic("区域名") {
                    execute<Player>{sender, context, argument ->
                        val loc1= setArea[sender.uniqueId]?.first
                        val loc2= setArea[sender.uniqueId]?.second
                        if(loc1!=null&&loc2!=null){
                            ConfigObject.config.setLocation("area.${argument}.pos1", adaptLocation(loc1))
                            ConfigObject.config.setLocation("area.${argument}.pos2", adaptLocation(loc2))
                            ConfigObject.config.set("area.${argument}.action","")
                            submitAsync {
                                try {
                                    ConfigObject.save()
                                    sender.info("保存完成")
                                }catch (e:Exception){
                                    e.printStackTrace()
                                    sender.info("&c保存出错".colored())
                                }
                            }
                        }
                    }
                }
            }
            literal("setAction"){
                dynamic ("区域名"){
                    execute<Player>{sender, context, argument ->
                        sender.inputBook("点击打开，输入脚本",disposable = true){
                            ConfigObject.config.set("area.${argument}.action",it)
                            submitAsync {
                                try {
                                    ConfigObject.save()
                                    sender.info("&a保存完成".colored())
                                }catch (e:Exception){
                                    e.printStackTrace()
                                    sender.info("&c保存出错".colored())
                                }
                            }
                        }
                    }
                }
            }

        }
    }
}