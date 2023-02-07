package me.kqn.elementdamage;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.damage.DamagingMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.damage.AttackMetadata;
import io.lumine.mythic.lib.damage.DamageMetadata;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.player.PlayerMetadata;
import io.lumine.mythic.lib.version.wrapper.VersionWrapper_1_18_R2;
import io.lumine.mythic.lib.element.Element;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@MythicMechanic(
        author = "kurt_kong",
        name = "elementdamage",
        aliases = {"ed"},
        description = ""
)
public class Skill extends DamagingMechanic implements ITargetedEntitySkill {
    protected Double amount;
    //protected final boolean ignoreMMOAttack;
    protected final Element element;

    public Skill(SkillExecutor manager, String line, MythicLineConfig config) {
        super(manager, line, config);
        this.amount=config.getDouble("amount",10d);
        this.element=Element.valueOf(config.getString("element","FIRE"));

    }

    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        if (!target.isDead() && target.getBukkitEntity() instanceof LivingEntity && !data.getCaster().isUsingDamageSkill() && !(target.getHealth() <= 0.0)&&data.getCaster().getEntity().getBukkitEntity() instanceof LivingEntity) {
            LivingEntity caster=(LivingEntity) data.getCaster().getEntity().getBukkitEntity();
            Double amt=amount;
            ElementAttackEntity ea=new ElementAttackEntity(caster,this.element,amt,(LivingEntity) target.getBukkitEntity());
            amt=amt+ea.getDamageModifier();

            if(amt>0.0)this.doDamage(data.getCaster(),target,amt);
            return SkillResult.SUCCESS;

        } else {
            return SkillResult.INVALID_TARGET;
        }
    }
}
