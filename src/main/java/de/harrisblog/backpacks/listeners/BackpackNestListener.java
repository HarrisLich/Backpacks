package de.harrisblog.backpacks.listeners;

import de.harrisblog.backpacks.Backpacks;
import de.harrisblog.backpacks.Util;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import static de.harrisblog.backpacks.Util.format;

public class BackpackNestListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if(event.getView().getTitle().equalsIgnoreCase(format(Backpacks.getPlugin().getConfig().getString("backpack.inventory_title")))){
            //check if the item they click in main hand
            if(Backpacks.getPlugin().getConfig().getString("options.nest_backpacks").equalsIgnoreCase("false")){
                if(event.getCurrentItem() != null && !event.getCurrentItem().getType().equals(Material.AIR) && Util.hasKey(event.getCurrentItem(), "backpack")){
                    event.getWhoClicked().sendMessage(format(Backpacks.getPlugin().getConfig().getString("messages.on_nest_backpack")));
                    event.setCancelled(true);
                }
            }
            //check for option

        }
    }
}
