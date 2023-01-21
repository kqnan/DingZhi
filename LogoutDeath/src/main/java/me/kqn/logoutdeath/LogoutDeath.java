package me.kqn.logoutdeath;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.BooleanFlag;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public final class LogoutDeath extends JavaPlugin  implements Listener , CommandExecutor {
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
    private YamlConfiguration config=null;
    private int interval=10;//秒
    private HashMap<UUID,Long> cd=new HashMap<>();
    private String msg="";



    @Override
    public void onEnable()  {
        // Plugin startup logic
        Bukkit.getPluginManager().registerEvents(this,this);
        Bukkit.getPluginCommand("ld").setExecutor(this);
        saveDefaultConfig();
        config=YamlConfiguration.loadConfiguration(new File("plugins/LogoutDeath/config.yml"));
        interval=config.getInt("interval",60);
        msg=config.getString("kick_message","");
        cd.clear();
    }
    @EventHandler
    public void login(PlayerLoginEvent event){
        Player player=event.getPlayer();
        if(cd.containsKey(player.getUniqueId())&&((System.currentTimeMillis()-cd.get(player.getUniqueId()))<interval* 1000L)){
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(ChatColor.translateAlternateColorCodes('&',msg));

        }
        else if(cd.containsKey(player.getUniqueId())&&((System.currentTimeMillis()-cd.get(player.getUniqueId()))>interval* 1000L)){
            cd.put(player.getUniqueId(),System.currentTimeMillis());
        }
        else if(!cd.containsKey(player.getUniqueId())){
            cd.put(player.getUniqueId(),System.currentTimeMillis());
        }
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
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(sender.isOp()){
            if(args.length>=1&&args[0].equalsIgnoreCase("reload")){
                config=YamlConfiguration.loadConfiguration(new File("plugins/LogoutDeath/config.yml"));
                interval=config.getInt("interval",60);
                msg=config.getString("kick_message","");
                cd.clear();
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&a[LogoutDeath]重载完毕"));
            }
        }
        return true;
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
