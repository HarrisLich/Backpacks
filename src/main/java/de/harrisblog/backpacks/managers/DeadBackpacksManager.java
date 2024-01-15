package de.harrisblog.backpacks.managers;

import com.mojang.datafixers.util.Pair;
import de.harrisblog.backpacks.Backpacks;
import de.harrisblog.backpacks.Util;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.entity.Entity;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.HashMap;

public class DeadBackpacksManager {

    private File deadBackpacksFolder;
    private Plugin plugin;
    private HashMap<String, Pair<Location, ItemStack>> deadBackpacks;
    private HashMap<String, ArmorStand> activeDeadBackpacks;

    public DeadBackpacksManager(){
        plugin = Backpacks.getPlugin();
        setup();
        loadActiveDeadBackpacks();

    }

    public void loadActiveDeadBackpacks(){
        activeDeadBackpacks = new HashMap<>();
        for(File file : deadBackpacksFolder.listFiles()){
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
            String backpackuuid = cfg.getString("inventory");
            Location location = cfg.getLocation("location");
            ItemStack helmet = cfg.getItemStack("helmet");
            for(org.bukkit.entity.Entity entity : location.getWorld().getNearbyEntities(location, 1, 1, 1)){
                if(Util.hasEntityKey(entity, "backpack")){
                    activeDeadBackpacks.put(backpackuuid, (ArmorStand) entity);
                }
            }

        }
    }

    public void setup(){
        if(!plugin.getDataFolder().exists()){
            plugin.getDataFolder().mkdir();
        }
        File dataFolder = plugin.getDataFolder();
        deadBackpacksFolder = new File(dataFolder, "deadbackpacks");
        if(!deadBackpacksFolder.exists()){
            deadBackpacksFolder.mkdirs();
        }
        //load deadbackpacks
        deadBackpacks = new HashMap<>();
        for(File file : deadBackpacksFolder.listFiles()){
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
            String backpackuuid = cfg.getString("inventory");
            Location location = cfg.getLocation("location");
            ItemStack helmet = cfg.getItemStack("helmet");
            Pair<Location, ItemStack> deathLocation = new Pair<>(location, helmet);
            deadBackpacks.put(backpackuuid, deathLocation);
        }

    }

    public HashMap<String, Pair<Location, ItemStack>> getDeadBackpacks() {
        return deadBackpacks;
    }

    public void spawnDeadBackpacks(){
        activeDeadBackpacks = new HashMap<>();
        for(String backpackuuid : deadBackpacks.keySet()){
            Pair<Location, ItemStack> deathLocation = deadBackpacks.get(backpackuuid);
            ArmorStand armorStand = (ArmorStand) deathLocation.getFirst().getWorld().spawnEntity(deathLocation.getFirst(), EntityType.ARMOR_STAND);
            armorStand.setHelmet(deathLocation.getSecond());
            armorStand.setInvisible(true);
            armorStand.setGravity(false);
            armorStand.setSmall(true);
            armorStand.setInvulnerable(true);
            Entity armorStandNms = ((CraftEntity) armorStand).getHandle();
            NBTTagCompound tagCompound = new NBTTagCompound();
            tagCompound.a("backpack", backpackuuid);
            armorStandNms.d(tagCompound);
            activeDeadBackpacks.put(backpackuuid, armorStand);
        }
    }

    public void clearAllDeadBackpacks(){
        for(String s : activeDeadBackpacks.keySet()){
            ArmorStand armorStand = activeDeadBackpacks.get(s);
            armorStand.remove();
        }
    }

    public File getDeadBackpacksFolder() {
        return deadBackpacksFolder;
    }
}
