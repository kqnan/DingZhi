package eos.moe.blueprint.gui;

import eos.moe.blueprint.Main;
import eos.moe.blueprint.config.Config;
import eos.moe.blueprint.utils.ItemUtils;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class BluePrintSetting implements Listener {
  public static void openGui(Player p, String key) {
    Inventory inv = Bukkit.createInventory((InventoryHolder)p, 36, "- " + key);
    ItemStack pane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)15);
    ItemStack confirm = new ItemStack(Material.NETHER_STAR);
    ItemUtils.setItemStackMeta(confirm, ", new String[] { ", ", ", ", "});
    for (int i = 9; i < 36; i++)
      inv.setItem(i, pane); 
    inv.setItem(20, null);
    inv.setItem(24, null);
    if (Config.containKey(key)) {
      List<ItemStack> input = Config.getBluePrintInput(key);
      int j = 0;
      for (ItemStack item : input) {
        inv.setItem(j, item);
        j++;
      } 
      inv.setItem(20, Config.getBluePrintBluePrint(key));
      inv.setItem(24, Config.getBluePrintOutput(key));
    } 
    inv.setItem(31, confirm);
    p.openInventory(inv);
  }
  
  @EventHandler
  public void click(InventoryClickEvent e) {
    if (e.getClickedInventory() != null && e.getClickedInventory().getTitle() != null && e.getClickedInventory().getTitle().contains(")) {
      Player p = (Player)e.getWhoClicked();
      if (e.getRawSlot() >= 9 && e.getRawSlot() <= 35 && e.getRawSlot() != 20 && e.getRawSlot() != 24)
        e.setCancelled(true); 
      if (e.getRawSlot() == 31) {
        Inventory inv = e.getClickedInventory();
        List<ItemStack> input = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
          if (inv.getItem(i) != null)
            input.add(inv.getItem(i)); 
        } 
        ItemStack blueprint = inv.getItem(20);
        ItemStack output = inv.getItem(24);
        if (input.size() == 0 || blueprint == null || output == null) {
          p.sendMessage(");
          return;
        } 
        if (Main.getBluePrintCount(blueprint) > 0) {
          p.sendMessage(");
          return;
        } 
        blueprint.setAmount(1);
        String key = e.getClickedInventory().getTitle().replaceAll("- ", "");
        Config.setBluePrintBluePrint(key, blueprint);
        Config.setBluePrintInput(key, input);
        Config.setBluePrintOutput(key, output);
        p.closeInventory();
        for (ItemStack item : input) {
          p.getInventory().addItem(new ItemStack[] { item.clone() });
        } 
        p.getInventory().addItem(new ItemStack[] { blueprint });
        p.sendMessage("" + key + " );
        p.sendMessage("/bp count  );
      } 
    } 
  }
}
