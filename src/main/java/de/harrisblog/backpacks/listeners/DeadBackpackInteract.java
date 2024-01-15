package de.harrisblog.backpacks.listeners;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import de.harrisblog.backpacks.Backpacks;
import de.harrisblog.backpacks.Util;
import de.harrisblog.backpacks.data.Backpack;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

public class DeadBackpackInteract implements Listener {
    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent event){
        Entity entity = event.getRightClicked();
        if(!(entity instanceof ArmorStand)) return;
        ArmorStand armorStand = (ArmorStand) entity;
        ItemStack helmet = armorStand.getHelmet();
        int num = Util.getInt(helmet, "backpack");
        String owneruuid = Util.getString(helmet, "owneruuid");
        String backpackuuid = new String(owneruuid + num);
        String owner = backpackuuid.substring(0,backpackuuid.length() -1);
        Player ownerPlayer = null;
        try{
            ownerPlayer = Backpacks.getPlugin().getServer().getPlayer(UUID.fromString(owner));
        }catch(Exception e){
            ownerPlayer = Backpacks.getPlugin().getServer().getOfflinePlayer(UUID.fromString(owner)).getPlayer();
        }
        if(Backpacks.getPlugin().getConfig().getString("options.disable_backpack_stealing").equalsIgnoreCase("true") && !owneruuid.equalsIgnoreCase(event.getPlayer().getUniqueId().toString())){
            event.setCancelled(true);
            event.getPlayer().sendMessage(Util.format(Backpacks.getPlugin().getConfig().getString("messages.on_try_stealing")));
            return;
        }
        boolean shouldCancel = false;
        for(ItemStack i : event.getPlayer().getInventory().getContents()){
            if(i != null && !i.getType().equals(Material.AIR) && Util.hasKey(i, "backpack")){
                if(helmet.equals(i)){
                    event.setCancelled(true);
                    return;
                }
            }
        }
        if(Util.hasKey(helmet, "backpack")){
            //give player the backpack, and remove the armor stand
            event.getPlayer().getInventory().addItem(helmet);
            Pair<Location, ItemStack> pair = Backpacks.getDeadBackpacksManager().getDeadBackpacks().get(backpackuuid);
            for(Entity e : event.getPlayer().getWorld().getNearbyEntities(pair.getFirst(), 4, 4, 4)){
                if(Util.hasEntityKey(e, "backpack") && e instanceof ArmorStand && ((ArmorStand) e).equals(armorStand)){
                    e.remove();
                }
            }

        }
        armorStand.remove();
        //delete deadbackpack file, remove it from hashmap
        for(File file : Backpacks.getDeadBackpacksManager().getDeadBackpacksFolder().listFiles()){
            if(backpackuuid.equalsIgnoreCase(file.getName().replace(".yml", ""))){
                file.delete();
                Backpacks.getDeadBackpacksManager().getDeadBackpacks().remove(backpackuuid);
            }
        }

        //Check for duplicate backpacks
        Bukkit.getScheduler().runTaskLater(Backpacks.getPlugin(), new Runnable() {
            @Override
            public void run() {
                int count = 0;
                Iterator<ItemStack> iterable = Arrays.asList(event.getPlayer().getInventory().getContents()).iterator();
                while(iterable.hasNext()){
                    ItemStack i = iterable.next();
                    if(i != null && !i.getType().equals(Material.AIR)){
                        if(i.equals(helmet)){
                            count += 1;
                        }
                    }
                }
                for(int i=1; i<count; i++){
                    event.getPlayer().getInventory().setItem(event.getPlayer().getInventory().first(helmet), new ItemStack(Material.AIR));
                }
            }
        }, 1L);

    }
}
