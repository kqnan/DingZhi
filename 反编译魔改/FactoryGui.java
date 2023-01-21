package eos.moe.blueprint.gui;

import eos.moe.blueprint.Main;
import eos.moe.blueprint.config.Config;
import eos.moe.blueprint.config.PlayerConfig;
import eos.moe.blueprint.utils.ItemUtils;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/* loaded from: FactoryGui.class */
public class FactoryGui implements Listener {
    public static void openGui(Player p) {
        Inventory inv = Bukkit.createInventory(p, 36, "图纸工厂");
        ItemStack pane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)15);
        ItemUtils.setItemStackMeta(pane, "§m");
        ItemStack paneok = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)0);
        ItemUtils.setItemStackMeta(paneok, "§a可用槽位", new String[]{"§m", "§8注意: 开始制作图纸时，图纸的剩余可用次数减一", "§m", "§a§l点击背包中的图纸来开始制造"});
        for (int i = 0; i < 36; i++) {
            inv.setItem(i, pane);
        }
        int slot = PlayerConfig.getSlot(p);
        ItemStack paneno = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)7);
        ItemUtils.setItemStackMeta(paneno, "§7未开启槽位");
        for (int i2 = 9; i2 < 27; i2++) {
            inv.setItem(i2, paneno);
        }
        for (int i3 = 9; i3 < 9 + slot; i3++) {
            inv.setItem(i3, paneok);
        }
        if (slot < 18) {
            ItemStack panejs = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)5);
            ItemUtils.setItemStackMeta(panejs, "§f§l解锁§a§l" + (slot + 1) + "§f§l号槽位", new String[]{"§m", "§7槽位可用于蓝图的制造", "§m", "§7价格: §f§l" + Config.getUnlockMoney(slot + 1) + " §3§l金币", "§7价格: §f§l" + Config.getUnlockPoint(slot + 1) + " §3§l点券", "§m", "§f?§a 左键§7点击 解锁一个工厂槽位"});
            inv.setItem(9 + slot, panejs);
        }
        ItemStack close = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)14);
        ItemUtils.setItemStackMeta(close, "§c关闭");
        inv.setItem(8, close);
        int i4 = 9;
        for (String item : PlayerConfig.getFactoryItems(p)) {
            String key = item.split("\\|")[0];
            long time = Long.parseLong(item.split("\\|")[1]);
            String dateStr = new SimpleDateFormat("yyy-MM-dd HH:mm:ss").format(new Date(time));
            ItemStack output = Config.getBluePrintOutput(key);
            ItemUtils.addLore(output, "§m");
            ItemUtils.addLore(output, "§a完成时间: §6" + dateStr);
            if (new Date().getTime() > time) {
                ItemUtils.addLore(output, "§a制作完成，左键取出物品");
            }
            i4++;
            inv.setItem(i4, output);
        }
        p.openInventory(inv);
    }

    @EventHandler
    public void click(InventoryClickEvent e) {
        if (e.getClickedInventory() != null && "图纸工厂".equals(e.getClickedInventory().getTitle())) {
            e.setCancelled(true);
            if (e.getRawSlot() < 54) {
                Player p = (Player) e.getWhoClicked();
                String itemName = ItemUtils.getItemName(e.getCurrentItem());
                if (e.getRawSlot() == 8) {
                    p.closeInventory();
                } else if (itemName.contains("§f§l解锁§a§l")) {
                    int slot = PlayerConfig.getSlot(p);
                    if (slot < 18) {
                        int money = Config.getUnlockMoney(slot + 1);
                        int point = Config.getUnlockPoint(slot + 1);
                        if (!playerHasMoney(p, money) || !playerHasPoint(p, point)) {
                            p.sendMessage("§7 [ §f终焉之歌 §7] §a解锁失败！");
                            p.sendMessage("§7 [ §f终焉之歌 §7] §a解锁需要 §f§l" + money + " §3§l金币");
                            p.sendMessage("§7 [ §f终焉之歌 §7] §a解锁需要 §f§l" + point + " §3§l点券");
                            return;
                        }
                        removePlayerMoney(p, money);
                        removePlayerPoint(p, point);
                        PlayerConfig.setSlot(p, slot + 1);
                        openGui(p);
                        p.sendMessage("§7 [ §f终焉之歌 §7] §a解锁成功！");
                        p.sendMessage("§7 [ §f终焉之歌 §7] §a消耗  §f§l" + money + " §3§l金币");
                        p.sendMessage("§7 [ §f终焉之歌 §7] §a消耗  §f§l" + point + " §3§l点券");
                    }
                } else {
                    int clickSlot = e.getRawSlot() - 9;
                    List<String> factoryItems = PlayerConfig.getFactoryItems(p);
                    if (clickSlot >= 0 && clickSlot < 18 && clickSlot < factoryItems.size()) {
                        String factoryItem = factoryItems.get(clickSlot);
                        String key = factoryItem.split("\\|")[0];
                        if (new Date().getTime() > Long.parseLong(factoryItem.split("\\|")[1])) {
                            int index = p.getInventory().firstEmpty();
                            ItemStack output = Config.getBluePrintOutput(key);
                            if (index == -1) {
                                p.sendMessage("§7 [ §f终焉之歌 §7] §a领取失败，需要身上有一个空槽位");
                                return;
                            }
                            p.getInventory().setItem(index, output);
                            p.sendMessage("§7 [ §f终焉之歌 §7] §a你领取了物品 " + ItemUtils.getItemName(output));
                            factoryItems.remove(clickSlot);
                            PlayerConfig.setFactoryItems(p, factoryItems);
                            openGui(p);
                            return;
                        }
                        p.sendMessage("§7 [ §f终焉之歌 §7] §a物品未制作完成，无法领取！");
                    }
                }
            }
        }
    }

    public boolean playerHasPoint(Player p, int money) {
        return Main.playerPoints != null && Main.playerPoints.getAPI().look(p.getUniqueId()) >= money;
    }

    public boolean playerHasMoney(Player p, int money) {
        return Main.eco != null && Main.eco.has(p, (double) money);
    }

    public void removePlayerMoney(Player p, int money) {
        Main.eco.withdrawPlayer(p, (double) money);
    }

    public void removePlayerPoint(Player p, int money) {
        Main.playerPoints.getAPI().take(p.getUniqueId(), money);
    }
}