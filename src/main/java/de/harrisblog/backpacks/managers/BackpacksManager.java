package de.harrisblog.backpacks.managers;

import de.harrisblog.backpacks.Backpacks;
import de.harrisblog.backpacks.data.Backpack;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.*;

public class BackpacksManager {
    private Plugin plugin;
    private File inventoriesFolder;
    private HashMap<String, Backpack> backpacks;

    public BackpacksManager(){
        plugin = Backpacks.getPlugin();
        setup();
        this.loadBackpacks();
    }

    public void loadBackpacks(){
        backpacks = new HashMap<>();
        for(File file : this.inventoriesFolder.listFiles()){
            String fileName = file.getName();
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
            List<ItemStack> loadItems = new ArrayList<>();
            if(!(cfg.getConfigurationSection("inventory").getKeys(false).size() == 0)){
                for(int i=0; i<cfg.getConfigurationSection("inventory").getKeys(false).size(); i++){
                    ItemStack item = cfg.getItemStack("inventory." + Integer.toString(i));
                    loadItems.add(item);
                }
            }
            Backpack backpack = new Backpack(fileName.replace(".yml", ""), loadItems.toArray(new ItemStack[0]), inventoriesFolder);
            backpacks.put(fileName.replace(".yml", ""), backpack);
        }
    }

    public void setup(){
        if(!plugin.getDataFolder().exists()){
            plugin.getDataFolder().mkdir();
        }
        File dataFolder = plugin.getDataFolder();
        inventoriesFolder = new File(dataFolder, "inventories");
        if(!inventoriesFolder.exists()){
            inventoriesFolder.mkdirs();
        }
    }

    public HashMap<String, Backpack> getBackpacks() {
        return backpacks;
    }

    public File getInventoriesFolder() {
        return inventoriesFolder;
    }
}
