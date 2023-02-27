package io.lumine.xikage.mythicmobs.commands.mobs;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitEntityType;
import io.lumine.xikage.mythicmobs.commands.CommandHelper;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.mobs.WorldScaling;
import io.lumine.xikage.mythicmobs.mobs.entities.MythicEntityType;
import io.lumine.xikage.mythicmobs.mobs.entities.SpawnReason;
import io.lumine.xikage.mythicmobs.utils.adventure.text.minimessage.Tokens;
import io.lumine.xikage.mythicmobs.utils.commands.Command;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

public class SpawnCommand extends Command<MythicMobs> {
    public SpawnCommand(Command<MythicMobs> parent) {
        super(parent);
    }

    public SpawnCommand(MythicMobs plugin) {
        super(plugin);
    }

    @Override // io.lumine.xikage.mythicmobs.utils.commands.Command
    public boolean onCommand(CommandSender sender, String[] args) {
        double level;
        String mob;
        Location loc = null;
        int amount = 1;
        boolean optAtTarget = false;
        boolean optAtPlayer = false;
        boolean optSilent = false;
        if (args.length == 0) {
            CommandHelper.sendError(sender, "You must specify a mob to spawn.");
            return true;
        }
        if (args != null && args.length > 1 && args[0].startsWith("-")) {
            if (args[0].contains("s")) {
                optSilent = true;
            }
            if (args[0].contains("t")) {
                optAtTarget = true;
            }
            if (args[0].contains("p")) {
                optAtPlayer = true;
            }
            args = (String[]) Arrays.copyOfRange(args, 1, args.length);
        }
        if (optAtPlayer) {
            String name = args[0];
            Player player = Bukkit.getPlayer(name);
            if (player != null) {
                if (optAtTarget) {
                    loc = player.getTargetBlock(MythicMobs.inst().getConfiguration().getTransparentBlocks(), 200).getRelative(BlockFace.UP).getLocation();
                } else {
                    loc = player.getLocation();
                }
                args = (String[]) Arrays.copyOfRange(args, 1, args.length);
            } else if (optSilent) {
                return true;
            } else {
                CommandHelper.sendError(sender, "Player " + name + " not found.");
                return true;
            }
        }
        if (args.length > 1) {
            try {
                amount = Integer.parseInt(args[1]);
            } catch (Exception e) {
                amount = 1;
            }
        }
        if (args.length > 2) {
            try {
                String[] part = args[2].split(",");
                World w = Bukkit.getWorld(part[0]);
                float x = Float.parseFloat(part[1]);
                float y = Float.parseFloat(part[2]);
                float z = Float.parseFloat(part[3]);
                float pitch = 0.0f;
                float yaw = 0.0f;
                if (part.length > 4) {
                    yaw = Float.parseFloat(part[4]);
                }
                if (part.length > 5) {
                    pitch = Float.parseFloat(part[5]);
                }
                if (w != null) {
                    loc = new Location(w, (double) x, (double) y, (double) z, yaw, pitch);
                }
            } catch (Exception e2) {
                CommandHelper.sendError(sender, "Invalid location specified for spawning a mob: location must be in format world,x,y,z,yaw,pitch");
                return true;
            }
        } else if (!optAtPlayer && (sender instanceof Player)) {
            if (optAtTarget) {
                loc = ((Player) sender).getTargetBlock(MythicMobs.inst().getConfiguration().getTransparentBlocks(), 200).getRelative(BlockFace.UP).getLocation();
            } else {
                loc = ((Player) sender).getLocation();
            }
        }
        if (loc != null) {
            if (args[0].contains(Tokens.SEPARATOR)) {
                String[] split = args[0].split(Tokens.SEPARATOR);
                mob = split[0];
                try {
                    level = Double.parseDouble(split[1]);
                } catch (Error | Exception e3) {
                    if (optSilent) {
                        return true;
                    }
                    CommandHelper.sendError(sender, "Invalid mob level supplied: must be a number.");
                    return true;
                }
            } else {
                mob = args[0];
                level = (double) WorldScaling.getLevelBonus(BukkitAdapter.adapt(loc));
            }
            if (MythicMobs.inst().getMobManager().getMythicMob(mob) != null) {
                ActiveMob l = null;
                for (int i = 0; i < amount; i++) {
                    l = MythicMobs.inst().getMobManager().spawnMob(mob, loc, level);
                }
                if (l != null) {
                    if (optSilent) {
                        return true;
                    }
                    //CommandHelper.sendSuccess(sender, "Spawned " + amount + "x " + mob + "&a!");
                    return true;
                } else if (optSilent) {
                    return true;
                } else {
                    CommandHelper.sendError(sender, "Failed to spawn mob. See console for more details.");
                    return true;
                }
            } else if (MythicEntityType.get(mob) != null) {
                for (int i2 = 0; i2 < amount; i2++) {
                    BukkitEntityType.getMythicEntity(mob).spawn(loc, SpawnReason.COMMAND);
                }
                if (optSilent) {
                    return true;
                }
                //CommandHelper.sendSuccess(sender, "Spawned " + amount + "x " + mob + "&a!");
                return true;
            } else {
                try {
                    EntityType type = EntityType.valueOf(mob);
                    for (int i3 = 0; i3 < amount; i3++) {
                        loc.getWorld().spawnEntity(loc, type);
                    }
                    if (optSilent) {
                        return true;
                    }
                    //CommandHelper.sendSuccess(sender, "Spawned " + amount + "x " + mob + "&a!");
                    return true;
                } catch (Exception e4) {
                    if (optSilent) {
                        return true;
                    }
                    CommandHelper.sendError(sender, "No mob type found with the name " + mob + ".");
                    return true;
                }
            }
        } else if (optSilent) {
            return true;
        } else {
            CommandHelper.sendError(sender, "Invalid location specified for spawning a mob: world does not exist.");
            return true;
        }
    }

    @Override // io.lumine.xikage.mythicmobs.utils.commands.Command
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args != null && args.length > 1 && args[0].startsWith("-")) {
            args = (String[]) Arrays.copyOfRange(args, 1, args.length);
        }
        if (args.length == 1) {
            return (List) StringUtil.copyPartialMatches(args[0], getPlugin().getMobManager().getMobNames(), new ArrayList());
        }
        return null;
    }

    @Override // io.lumine.xikage.mythicmobs.utils.commands.Command
    public String getPermissionNode() {
        return "mythicmobs.command.mobs.spawn";
    }

    @Override // io.lumine.xikage.mythicmobs.utils.commands.Command
    public boolean isConsoleFriendly() {
        return true;
    }

    @Override // io.lumine.xikage.mythicmobs.utils.commands.Command
    public String getName() {
        return "spawn";
    }

    @Override // io.lumine.xikage.mythicmobs.utils.commands.Command
    public String[] getAliases() {
        return new String[]{"s"};
    }
}