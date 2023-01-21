package me.kqn.TeamTp

import org.bukkit.event.player.PlayerInteractEvent
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common5.Baffle
import taboolib.platform.util.isLeftClick
import taboolib.platform.util.isLeftClickBlock
import taboolib.platform.util.isRightClickBlock
import java.util.concurrent.TimeUnit

object SetArea {
    val baffle=Baffle.of(100,TimeUnit.MILLISECONDS)
    @SubscribeEvent
    fun click(e:PlayerInteractEvent){
        var item=e.player.inventory.itemInMainHand
        if(item == TeamTp.item.also { it.amount=item.amount }){
            if(e.isLeftClickBlock()){
                e.player.info("第一角点设置完毕")
                var pair=TeamTp.setArea.getOrDefault(e.player.uniqueId,Pair(null,null))
                var newpair=Pair(e.clickedBlock?.location?.clone(),pair.second?.clone())
                TeamTp.setArea.put(e.player.uniqueId,newpair)
                e.isCancelled=true
            }
            else if(e.isRightClickBlock()){
                if(!baffle.hasNext())return
                baffle.next()
                e.player.info("第二角点设置完毕")
                var pair=TeamTp.setArea.getOrDefault(e.player.uniqueId,Pair(null,null))
                var newpair=Pair(pair.first?.clone(),e.clickedBlock?.location?.clone())
                TeamTp.setArea.put(e.player.uniqueId,newpair)
                e.isCancelled=true

            }
        }
    }
    @Awake(LifeCycle.DISABLE)
    private fun disable(){
        baffle.resetAll()
    }
}