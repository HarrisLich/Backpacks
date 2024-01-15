package de.harrisblog.backpacks.data;

import de.harrisblog.backpacks.Backpacks;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static de.harrisblog.backpacks.Util.format;

public class Backpack {
    private int size = 27;
    private String viewName;
    private ItemStack[] items;
    private Inventory inventory;
    private Player inventoryOwner;
    private File inventoriesFolder;
    private String uid;


    public Backpack(String uuid, ItemStack[] items, File inventoriesFolder){
        this.uid = uuid;
        this.viewName = format(Backpacks.getPlugin().getConfig().getString("backpack.inventory_title"));
        this.inventoryOwner = this.loadInventoryOwner(uuid);
        this.inventoriesFolder = inventoriesFolder;
        this.inventory = createInvetory();
        this.items = items;
    }

    public Player loadInventoryOwner(String uuid){
        try{
            return Backpacks.getPlugin().getServer().getPlayer(UUID.fromString(uuid.substring(0, uuid.length()-1)));
        }catch(Exception err){
            return Backpacks.getPlugin().getServer().getOfflinePlayer(UUID.fromString(uuid.substring(0, uuid.length()-1))).getPlayer();
        }
    }

    public Player playerFromOfflinePlayer(OfflinePlayer p){
        return p.getPlayer();
    }



    private Inventory createInvetory(){
        boolean hasInventoryFile = false;
        File invFile = null;
        for(File file : inventoriesFolder.listFiles()) {
            if (file.getName().contains(uid)) {
                hasInventoryFile = true;
                invFile = file;
            }
        }
        if(hasInventoryFile){
            //load items into a ItemStack[] then into a inventory to return;
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(invFile);
            Inventory inv = Bukkit.createInventory(inventoryOwner, size, viewName);
            List<ItemStack> items = new ArrayList<>();
            for(int i=0; i<cfg.getConfigurationSection("inventory").getKeys(false).size();i++){
                ItemStack item = cfg.getItemStack("inventory." + Integer.toString(i));
                items.add(item);
            }

            for(int i=0; i<items.size(); i++){
                inv.setItem(i, items.get(i));
            }
            return inv;

        }else{
            return Bukkit.createInventory(inventoryOwner, size, viewName);
        }
    }

    public int getSize() {
        return size;
    }

    public String getViewName() {
        return viewName;
    }

    public ItemStack[] getItems() {
        return items;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Player getInventoryOwner() {
        return inventoryOwner;
    }
}
