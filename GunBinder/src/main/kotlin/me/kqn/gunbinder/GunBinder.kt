package me.kqn.gunbinder

import com.shampaggon.crackshot.events.WeaponShootEvent
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerPickupItemEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import taboolib.common.platform.Plugin
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.info
import taboolib.platform.util.hasLore
import taboolib.platform.util.isAir
import taboolib.platform.util.modifyMeta
import java.util.*

object GunBinder : Plugin() {

    override fun onEnable() {

    }
    private fun bind(item: ItemStack, p: Player){
        item.modifyMeta<ItemMeta> {
            this.persistentDataContainer.set(NamespacedKey.fromString("GunBinder")!!, PersistentDataType.STRING,p.uniqueId.toString())
        }
    }
    private fun isBind(item:ItemStack,id: UUID):Boolean{
        val bindID=item.itemMeta?.persistentDataContainer?.get(NamespacedKey.fromString("GunBinder")!!, PersistentDataType.STRING)?:return false
        if(bindID==id.toString())return true
        return  false

    }



    @SubscribeEvent
    fun noPick(e:EntityPickupItemEvent){
        if(!isBind(item = e.item.itemStack,e.entity.uniqueId)){
            e.isCancelled=true
        }
    }
    @SubscribeEvent
    fun noDrop(e:PlayerDropItemEvent){
        val item=e.itemDrop.itemStack
        if(isBind(item,e.player.uniqueId)){
            e.isCancelled=true
        }
    }
    @SubscribeEvent
    fun shootevent(e:WeaponShootEvent){
        val item=e.player.inventory.itemInMainHand
        info("Íæ¼Ò${e.player.name} Éä»÷")
        if(item.isAir)return
        for (key in ConfigFile.config.getKeys(false)) {
            if(item.hasLore(ConfigFile.config.getString(key)?:continue)){
                info("°ó¶¨")
                bind(item,e.player)

                return
            }
        }
    }
}