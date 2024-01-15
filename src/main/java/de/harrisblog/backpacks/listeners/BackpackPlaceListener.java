package de.harrisblog.backpacks.listeners;

import de.harrisblog.backpacks.Util;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class BackpackPlaceListener implements Listener {
    @EventHandler
    public void onPlace(BlockPlaceEvent e){
        ItemStack i = e.getItemInHand();
        if(i != null && i.hasItemMeta() && i.getItemMeta().hasLore()){
            if(Util.hasKey(i, "backpack")){
                e.setCancelled(true);
            }
        }

    }
}
