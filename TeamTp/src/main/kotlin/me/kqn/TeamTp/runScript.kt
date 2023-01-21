package me.kqn.TeamTp

import org.bukkit.Location
import org.bukkit.block.Sign
import org.bukkit.event.player.PlayerInteractEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.adaptLocation
import taboolib.common.platform.function.submitAsync
import taboolib.common.util.asList
import taboolib.module.chat.colored
import taboolib.module.configuration.util.getLocation
import taboolib.platform.util.isRightClickBlock
import taboolib.platform.util.onlinePlayers
import taboolib.platform.util.toBukkitLocation
import java.util.concurrent.CopyOnWriteArrayList

object runScript {
    private val isProcessing=CopyOnWriteArrayList<String>()
    @SubscribeEvent
    fun run(e:PlayerInteractEvent){
        if(e.isRightClickBlock()){
            var block=e.clickedBlock?:return
            debug((block.state is Sign).toString())
            if(block.state is Sign){
                var sign=block.state as Sign
                for (line in sign.lines) {
                    debug(line)
                    debug((line == (ConfigObject.config.getString("sign")?.colored() ?: return)).toString())
                }
                for (line in sign.lines) {
                    if(line == (ConfigObject.config.getString("sign")?.colored() ?: return)){
                        for (key in ConfigObject.config.getConfigurationSection("area")?.getKeys(false)?:return) {
                            var loc1=ConfigObject.config.getLocation("area.${key}.pos1")?.toBukkitLocation()?:continue
                            var loc2=ConfigObject.config.getLocation("area.${key}.pos2")?.toBukkitLocation()?:continue
                            debug(sign.location.betweeen(loc1,loc2).toString())
                            if(!sign.location.betweeen(loc1,loc2))continue
                            var actions=ConfigObject.config.getStringList("area.${key}.action")
                            if(actions.isEmpty())return
                            if(isProcessing.contains(key))return
                            isProcessing.add(key)
                            submitAsync {
                                actions.forEach {
                                    for (onlinePlayer in onlinePlayers) {
                                        debug(onlinePlayer.location.betweeen(loc1,loc2).toString())
                                        if(onlinePlayer.location.betweeen(loc1,loc2)){
                                            it.eval(onlinePlayer)?.get()
                                        }
                                    }
                                }
                                isProcessing.remove(key)
                            }
                            return
                        }
                    }
                }
            }

        }
    }
}