package me.kqn.elementdamage;

import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.element.Element;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class ElementDamage extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Plugin startup logic

        Bukkit.getPluginManager().registerEvents(this,this);
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
