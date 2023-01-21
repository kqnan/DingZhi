package me.kqn.TeamTp

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.chat.colored
import taboolib.module.kether.KetherShell
import taboolib.platform.util.onlinePlayers
import java.util.concurrent.CompletableFuture
import kotlin.math.max
import kotlin.math.min
fun Player.eval(kether:List<String>){
    for (s in kether) {
        s.eval(this)
    }
}
fun debug(msg:String){
    if(ConfigObject.config.getBoolean("debug")) {
        Bukkit.getLogger().info(msg)
        for (onlinePlayer in onlinePlayers) {
            if(onlinePlayer.isOp)onlinePlayer.sendMessage(msg)
        }
    }
}
fun String.eval(player:Player):CompletableFuture<Any?>?{
    try {
       return KetherShell.eval(this,cacheScript = true, sender = adaptPlayer(player))
    }catch (e:Exception){
        e.printStackTrace()
    }
    return null
}
fun Player.info(msg:String){
    this.sendMessage("&a[TeamTp] $msg".colored())
}
fun Location.betweeen(loc1: Location, loc2: Location):Boolean{
    if(this.x>= min(loc1.x,loc2.x) &&this.x<= max(loc1.x,loc2.x)){
        if(this.y>= min(loc1.y,loc2.y) &&this.y<= max(loc1.y,loc2.y)){
            if(this.z>= min(loc1.z,loc2.z) &&this.z<= max(loc1.z,loc2.z)){
                return true;
            }
            return false;
        }
        else return false
    }else return false
}
fun taboolib.common.util.Location.toBukkit():Location{
    return Location(Bukkit.getWorld(this.world!!),this.x,this.y,this.z)
}