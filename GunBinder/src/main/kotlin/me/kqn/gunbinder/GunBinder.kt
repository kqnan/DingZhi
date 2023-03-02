package me.kqn.gunbinder

import com.shampaggon.crackshot.events.WeaponPreShootEvent
import com.shampaggon.crackshot.events.WeaponPrepareShootEvent
import com.shampaggon.crackshot.events.WeaponShootEvent
import de.tr7zw.changeme.nbtapi.NBTItem
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerPickupItemEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import taboolib.common.platform.Plugin
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.info
import taboolib.common.platform.function.submit
import taboolib.module.chat.colored
import taboolib.platform.util.*

import java.util.*

object GunBinder : Plugin() {

    override fun onEnable() {
        //每秒检测玩家的背包，对枪械进行绑定
        submit(period=20){
            for (onlinePlayer in onlinePlayers) {
                var items=onlinePlayer.inventory
                items.forEach {
                    if(it!=null&&it.isNotAir())
                    {
                        if(hasLore(it)&&!hasNBTkey(it)){
                            bind(it,onlinePlayer)
                            setLore(it,onlinePlayer)
                        }
                        else if (isBind(it,onlinePlayer.uniqueId)&&!it.hasLore("&7所有者：${onlinePlayer.name}".colored())){
                            setLore(it,onlinePlayer)
                        }
                    }

                }
            }
        }
    }
    private fun setLore(item:ItemStack,p:Player){
        val str="&7所有者：${p.name}".colored()
        item.modifyLore {
            this.remove(str)
            this.add(str) }
    }
    private fun hasLore(item: ItemStack):Boolean{
        for (key in ConfigFile.config.getKeys(false)) {
            val lore=ConfigFile.config.getString(key)?:continue
            if(item.hasLore(lore))return true
        }
        return false;
    }
    private fun hasNBTkey(item: ItemStack):Boolean{
        val nbtItem=NBTItem(item)
        return nbtItem.hasKey("GunBinder")
    }
    private fun bind(item: ItemStack, p: Player){
        var nbtitem=NBTItem(item)
        nbtitem.setString("GunBinder",p.uniqueId.toString())
        nbtitem.applyNBT(item)
    }
    private fun isBind(item:ItemStack,id: UUID):Boolean{
        if(item.isAir)return false
        val bindID=NBTItem(item).getString("GunBinder")
        if(bindID==id.toString())return true
        return false

    }
    //阻止开火
    @SubscribeEvent
    fun noShoot(e:WeaponPreShootEvent){
        val item=e.player.inventory.itemInMainHand
        if(item.isAir())return
        val nbti=NBTItem(item)
        if(!nbti.hasKey("GunBinder"))return
        if(!isBind(item,e.player.uniqueId))
        {
            val owner=Bukkit.getPlayer(UUID.fromString(nbti.getString("GunBinder")))
            owner?.let{
                e.player.sendMessage(MessageFile.notowner.replace("%player%",it.name).colored())
            }
            e.isCancelled=true
        }
    }
    @SubscribeEvent
    fun noShoot2(e:WeaponPrepareShootEvent){
        val item=e.player.inventory.itemInMainHand
        if(item.isAir())return
        val nbti=NBTItem(item)
        if(!nbti.hasKey("GunBinder"))return
        if(!isBind(item,e.player.uniqueId))
        {
            e.isCancelled=true
        }
    }

}