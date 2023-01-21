package me.kqn.logoutdeath;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.BooleanFlag;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class LogoutDeath extends JavaPlugin  implements Listener {
    private WorldGuardPlugin worldGuardPlugin=null;
    private RegionContainer regionContainer=null;
    @Override
    public void onLoad(){
        BooleanFlag booleanFlag=Flags. DeadOnLogout;
        this.worldGuardPlugin = (WorldGuardPlugin)this.getServer().getPluginManager().getPlugin("WorldGuard");
        this.regionContainer=worldGuardPlugin.getRegionContainer();
        try {
            if(worldGuardPlugin.getFlagRegistry().get("DeadOnLogout")!=null)return;
            worldGuardPlugin.getFlagRegistry().register(booleanFlag);
        }catch (Exception e){
            e.printStackTrace();
            Bukkit.getLogger().warning("[LogoutDeath]未开启WorldGuard,安全区功能将不可用");
        }
    }
    @Override
    public void onEnable()  {
        // Plugin startup logic
        Bukkit.getPluginManager().registerEvents(this,this);
    }
    @EventHandler
    public void onLogout(PlayerQuitEvent event){
        if(worldGuardPlugin!=null){
            Player player=event.getPlayer();
            LocalPlayer localPlayer = this.worldGuardPlugin.wrapPlayer(player);
            ApplicableRegionSet regions = this.regionContainer.createQuery().getApplicableRegions(player.getLocation());
            Boolean isdead = (Boolean)regions.queryValue(localPlayer,Flags.DeadOnLogout);
            if (isdead != null&&isdead) {
                player.setHealth(0.0);
            }
        }
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
