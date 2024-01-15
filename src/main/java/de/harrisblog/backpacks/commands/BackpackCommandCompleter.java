package de.harrisblog.backpacks.commands;

import de.harrisblog.backpacks.Backpacks;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BackpackCommandCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {

        if(strings.length == 1){
            List<String> args = new ArrayList<>();
            if(commandSender.hasPermission("backpacks.reload")) args.add("reload");
            if(commandSender.hasPermission("backpacks.clear")) args.add("clear");
            if(commandSender.hasPermission("backpacks.reset")) args.add("reset");
            return args;
        }else if(strings.length == 2 && (strings[0].equalsIgnoreCase("clear") || strings[0].equalsIgnoreCase("reset"))){
            List<String> args = new ArrayList<>();
            for(Player p : Backpacks.getPlugin().getServer().getOnlinePlayers()){
                args.add(p.getName());
            }
            return args;
        }
        return null;
    }
}
