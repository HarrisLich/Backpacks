package de.harrisblog.backpacks.commands;

import de.harrisblog.backpacks.Backpacks;
import de.harrisblog.backpacks.Util;
import de.harrisblog.backpacks.data.Backpack;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class BackpackCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(strings.length == 2 && strings[0].equalsIgnoreCase("reset") && commandSender.hasPermission("backpacks.reset")){
            if(!(commandSender instanceof Player)) return false;
            Player p = Backpacks.getPlugin().getServer().getPlayer(strings[1]);
            if(Backpacks.getBackpacksManager().getBackpacks().containsKey(p.getUniqueId().toString())){
                Backpack backpack = Backpacks.getBackpacksManager().getBackpacks().get(p.getUniqueId().toString());
                backpack.getInventory().clear();
            }
            String uuid = p.getUniqueId().toString();
            for(File file : Backpacks.getBackpacksManager().getInventoriesFolder().listFiles()){
                if(file.getName().contains(uuid)){
                    file.delete();
                    p.sendMessage(Util.format(Backpacks.getPlugin().getConfig().getString("messages.on_bp_reset")));
                }
            }
            return true;
        }else if(strings.length == 2 && strings[0].equalsIgnoreCase("clear") && commandSender.hasPermission("backpacks.clear")){
            if(!(commandSender instanceof Player)) return false;
            Player p = Backpacks.getPlugin().getServer().getPlayer(strings[1]);
            if(Backpacks.getBackpacksManager().getBackpacks().containsKey(p.getUniqueId().toString())){
                Backpack backpack = Backpacks.getBackpacksManager().getBackpacks().get(p.getUniqueId().toString());
                backpack.getInventory().clear();
            }
            String uuid = p.getUniqueId().toString();
            for(File file : Backpacks.getBackpacksManager().getInventoriesFolder().listFiles()){
                if(file.getName().contains(uuid)){
                    File holder = file;
                    file.delete();
                    File newFile = new File(Backpacks.getBackpacksManager().getInventoriesFolder(), holder.getName());
                    FileConfiguration cfg = YamlConfiguration.loadConfiguration(newFile);
                    cfg.createSection("inventory");
                    p.sendMessage(Util.format(Backpacks.getPlugin().getConfig().getString("messages.on_bp_clear")));
                }
            }
            return true;
        }else if(strings.length == 1 && strings[0].equalsIgnoreCase("reload")){
            Util.saveAllBackpacks();
            Backpacks.getPlugin().reloadConfig();
            Backpacks.getBackpacksManager().loadBackpacks();
            commandSender.sendMessage(Util.format(Backpacks.getPlugin().getConfig().getString("messages.reload")));
            return true;
        }else if(strings.length == 1 && strings[0].equalsIgnoreCase("help") && commandSender.hasPermission("backpacks.help")){
            List<String> helpStrings = Backpacks.getPlugin().getConfig().getStringList("messages.help");
            for(String str : helpStrings){
                commandSender.sendMessage(Util.format(str));
            }
        }
        return true;
    }
}
