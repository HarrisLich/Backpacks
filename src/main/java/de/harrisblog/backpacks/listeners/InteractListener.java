package de.harrisblog.backpacks.listeners;

import de.harrisblog.backpacks.Backpacks;
import de.harrisblog.backpacks.Util;
import de.harrisblog.backpacks.data.Backpack;
import org.apache.logging.log4j.core.config.ConfigurationFileWatcher;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;

public class InteractListener implements Listener {
    @EventHandler
    public void onRightClick(PlayerInteractEvent event){
        if(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
            ItemStack i = event.getPlayer().getItemInHand();
            if(i != null && i.hasItemMeta() && i.getItemMeta().hasLore()){
                if(Util.hasKey(i, "backpack")){
                    if(Util.hasKey(i, "backpack")){
                        //grab nbt data
                        int backpackNum = Util.getInt(i, "backpack");

                        //open inventory
                        Player player = event.getPlayer();
                        String backpackuuid = Util.getString(i, "owneruuid") + Integer.toString(backpackNum);
                        Backpack backpack = Backpacks.getBackpacksManager().getBackpacks().get(backpackuuid);
                        if(backpack == null){
                            return;
                        }
                        event.getPlayer().openInventory(backpack.getInventory());

                    }
                }
            }
        }
    }
}
