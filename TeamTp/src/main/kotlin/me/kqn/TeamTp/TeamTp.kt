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
        item=ItemBuilder(Material.STICK).also { it.name="&f����ѡȡ����".colored()
        it.lore.add("&7������-���õ�һ�ǵ�".colored())
            it.lore.add("&7�Ҽ����-���õڶ��ǵ�".colored())
            it.lore.add("&7������Ϻ�����/tmtp save ��������".colored())
        }.build()
        regcmd()
    }

    private fun regcmd(){
        command(name="TeamTp", aliases = listOf("tmtp"), permission = "TeamTp.use", permissionDefault = PermissionDefault.OP){
            execute<Player>{sender, context, argument ->
                sender.sendMessage("&a/tmtp tool ��ȡ��������Ĺ���".colored())
                sender.sendMessage("&a/tmtp save <������>  ��������".colored())
                sender.sendMessage("&a/tmtp setAction <������> Ϊָ���������ýű�".colored())


            }
            literal("tool"){
                execute<Player>{sender, context, argument ->
                    sender.giveItem(item.clone())
                }
            }
            literal("save"){
                dynamic("������") {
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
                                    sender.info("�������")
                                }catch (e:Exception){
                                    e.printStackTrace()
                                    sender.info("&c�������".colored())
                                }
                            }
                        }
                    }
                }
            }
            literal("setAction"){
                dynamic ("������"){
                    execute<Player>{sender, context, argument ->
                        sender.inputBook("����򿪣�����ű�",disposable = true){
                            ConfigObject.config.set("area.${argument}.action",it)
                            submitAsync {
                                try {
                                    ConfigObject.save()
                                    sender.info("&a�������".colored())
                                }catch (e:Exception){
                                    e.printStackTrace()
                                    sender.info("&c�������".colored())
                                }
                            }
                        }
                    }
                }
            }

        }
    }
}