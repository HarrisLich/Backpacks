package de.harrisblog.backpacks.listeners;

import de.harrisblog.backpacks.Backpacks;
import de.harrisblog.backpacks.Util;
import de.harrisblog.backpacks.data.Backpack;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

import javax.xml.transform.Result;
import java.io.File;
import java.io.IOException;

public class CraftListener implements Listener {
    @EventHandler
    public void onCraft(CraftItemEvent event){
        boolean hasCraftedBackpack = false;
        if(event.getInventory().getResult().equals(Util.getBackpackItemStack())){
            int fileCount = 0;
            for(File file : Backpacks.getBackpacksManager().getInventoriesFolder().listFiles()){
                if(file.getName().contains(((Player) event.getWhoClicked()).getUniqueId().toString())){
                    fileCount += 1;
                }
            }
            int max_backpacks = Backpacks.getPlugin().getConfig().getInt("options.max_backpacks");
            if(max_backpacks == -1 ) max_backpacks = Integer.MAX_VALUE;
            if(fileCount < max_backpacks){
                //create the new file for the new backpack
                File inventoryConfig = new File(Backpacks.getBackpacksManager().getInventoriesFolder(), new String(((Player) event.getWhoClicked()).getUniqueId() + Integer.toString(fileCount) + ".yml"));
                if(!inventoryConfig.exists()){
                    try{
                        inventoryConfig.createNewFile();
                    }catch(Exception e){
                        Backpacks.getPlugin().getLogger().info(ChatColor.RED + "Failed to create backpack inventory config!");
                    }

                }
                //save the yml
                FileConfiguration cfg = YamlConfiguration.loadConfiguration(inventoryConfig);
                cfg.createSection("inventory");
                try{
                    cfg.save(inventoryConfig);
                    Backpacks.getBackpacksManager().getBackpacks().put(new String(((Player)event.getWhoClicked()).getUniqueId().toString() + Integer.toString(fileCount)), new Backpack(((Player)event.getWhoClicked()).getUniqueId().toString(), new ItemStack[] {}, Backpacks.getBackpacksManager().getInventoriesFolder() ));
                }catch(IOException e){
                    Backpacks.getPlugin().getLogger().info(ChatColor.RED + "Failed to save yml config!");
                }
                //add nbt data
                ItemStack i = event.getCurrentItem();
                i = Util.setNBT(i,"backpack", fileCount);
                i = Util.setNBT(i, "owneruuid", event.getWhoClicked().getUniqueId().toString());
                event.setCurrentItem(i);
            }else{
                event.setCancelled(true);
                event.getWhoClicked().sendMessage(Backpacks.getPlugin().getConfig().getString("messages.max_backpacks"));
            }
        }
    }
}
