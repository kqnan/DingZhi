package me.kqn.elementdamage;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//



import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.player.PlayerMetadata;

import java.util.*;

import io.lumine.mythic.lib.element.Element;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.player.PlayerData.CooldownType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;


public class ElementAttackEntity {
    private final Map<Element, Double> relative = new HashMap();
    private final Map<Element, Double> absolute = new HashMap();
    private final LivingEntity target;
    private final double regularDamage;
    private final double initialDamage;
    private static final Random RANDOM = new Random();
    private final LivingEntity attacker;

    public ElementAttackEntity(LivingEntity attacker, Element ele, double initialDamage, LivingEntity target) {
        this.initialDamage = initialDamage;
        //this.playerData = PlayerData.get(attacker.getPlayer());
        //this.attacker = attacker;
        this.target = target;
        double regularDamage = initialDamage;
        Collection<Element> var8 = Element.values();
       // int var9 = var8.length;
        this.attacker=attacker;

            Element element = ele;
            double relativeDamage = 100.0;

                double flatElemental = relativeDamage / 100.0 * initialDamage;
                this.relative.put(element, relativeDamage);
                this.absolute.put(element, flatElemental);
                regularDamage -= flatElemental;



        this.regularDamage = regularDamage;
    }

    public double getDamageModifier() {
        ItemStack[] var1 = this.target.getEquipment().getArmorContents();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            ItemStack equip = var1[var3];
            if(equip==null)continue;
            NBTItem nbtEquip = MythicLib.plugin.getVersion().getWrapper().getNBTItem(equip);
            if (nbtEquip.getType() != null) {
                Iterator var6 = this.absolute.keySet().iterator();

                while(var6.hasNext()) {
                    Element element = (Element)var6.next();

                    double defense = nbtEquip.getStat(element.getId() + "_DEFENSE") / 100.0;
                    if (defense > 0.0) {
                        this.relative.put(element, (Double)this.relative.get(element) * (1.0 - defense));
                        this.absolute.put(element, (Double)this.absolute.get(element) * (1.0 - defense));
                    }
                }
            }
        }


        double partialDamage;



        double finalDamage = this.regularDamage;
        Iterator var15 = this.absolute.keySet().iterator();

        while(var15.hasNext()) {
            Element element = (Element)var15.next();
            partialDamage = (Double)this.absolute.get(element);
            if (partialDamage > 0.0) {
                finalDamage += partialDamage;
                //element.ge.displayParticle(this.target);
            }
        }


      //    System.out.println(finalDamage+"   "+initialDamage);
        return finalDamage - this.initialDamage;
    }
}
