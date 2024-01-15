package de.harrisblog.backpacks.listeners;

import com.mojang.datafixers.util.Pair;
import de.harrisblog.backpacks.Backpacks;
import de.harrisblog.backpacks.Util;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.entity.Entity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DeathEvent implements Listener {
    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        Player player = event.getEntity();
        Location deathLocation = player.getLocation();
        Location deadBackpackLocation = deathLocation.add(0.0, -0.7, 0.0);
        Location holder = deadBackpackLocation;
        ItemStack[] inv = player.getInventory().getContents();
        for(ItemStack i : inv){
            if(i != null && !i.getType().equals(Material.AIR) && Util.hasKey(i, "backpack")){
                deadBackpackLocation = holder;
                //item is a backpack -> spawn armorstand
                double rndX = Math.random() * 1.5;
                double rndZ = Math.random() * 1.5;
                deadBackpackLocation.add(rndX, 0.0, rndZ);
                int data = Util.getInt(i, "backpack");
                String owneruuid = Util.getString(i, "owneruuid");
                String backpackuuid = new String(owneruuid + data);
                ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(deadBackpackLocation, EntityType.ARMOR_STAND);
                armorStand.setHelmet(i);
                armorStand.setSmall(true);
                armorStand.setInvisible(true);
                armorStand.setInvulnerable(true);
                armorStand.setGravity(false);
                Entity armorStandNms = ((CraftEntity) armorStand).getHandle();
                NBTTagCompound tagCompound = new NBTTagCompound();
                tagCompound.a("backpack", backpackuuid);
                armorStandNms.d(tagCompound);
                ArmorStand ent = (ArmorStand) armorStandNms.getBukkitEntity();
                Backpacks.getPlugin().getLogger().info(Boolean.toString(Util.hasEntityKey(ent, backpackuuid)));

                //Create the deadbackpack file (holds the location, and inventory information)
                Util.createDeadBackpackFile(backpackuuid, deadBackpackLocation, i);
                //add to deadbackpackmanager hashmap
                Backpacks.getDeadBackpacksManager().getDeadBackpacks().put(backpackuuid, new Pair<>(deadBackpackLocation, i));
            }
        }
        List<ItemStack> drops = event.getDrops();
        Iterator<ItemStack> iterator = drops.iterator();
        while (iterator.hasNext()) {
            ItemStack element = iterator.next();
            if (Util.hasKey(element, "backpack")) {
                // Safely remove the current element
                iterator.remove();
            }
        }
    }
}
