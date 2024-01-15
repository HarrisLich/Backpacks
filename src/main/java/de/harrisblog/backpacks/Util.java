package de.harrisblog.backpacks;

import de.harrisblog.backpacks.data.Backpack;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import javax.naming.Name;
import javax.xml.stream.events.Namespace;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Util {

    public static void createDeadBackpackFile(String backpackuuid, Location deathLocation, ItemStack helmet){
        File folder = Backpacks.getDeadBackpacksManager().getDeadBackpacksFolder();
        File deadBackpack = new File(folder, new String(backpackuuid + ".yml"));
        if(!deadBackpack.exists()){
            try{
                deadBackpack.createNewFile();
            }catch(IOException err){
                Backpacks.getPlugin().getLogger().info("Unable to create dead backpack file!");
            }
        }
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(deadBackpack);
        cfg.set("inventory", backpackuuid);
        cfg.set("location", deathLocation);
        cfg.set("helmet", helmet);

        try{
            cfg.save(deadBackpack);
        }catch(IOException err){
            Backpacks.getPlugin().getLogger().info("Unable to save dead backpack config!");
        }
    }

    public static boolean hasEntityKey(Entity entity, String key){
        net.minecraft.world.entity.Entity e = ((CraftEntity) entity).getHandle();
        return e.a(key);
    }

    public static ItemStack setNBT(ItemStack item , String key, int i){
        net.minecraft.world.item.ItemStack itemStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tagCompound = itemStack.v();
        tagCompound.a(key, i);
        itemStack.b(tagCompound);
        item = CraftItemStack.asBukkitCopy(itemStack);
        return item;
    }
    public static ItemStack setNBT(ItemStack item , String key, String s){
        net.minecraft.world.item.ItemStack itemStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tagCompound = itemStack.v();
        tagCompound.a(key, s);
        itemStack.b(tagCompound);
        item = CraftItemStack.asBukkitCopy(itemStack);
        return item;
    }

    public static boolean hasKey(ItemStack item, String key){
        net.minecraft.world.item.ItemStack itemStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tagCompound = itemStack.v();
        try{
            return tagCompound.e(key);
        }catch(Exception e){
            return false;
        }

    }

    public static int getInt(ItemStack item, String key){
        net.minecraft.world.item.ItemStack itemStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tagCompound = itemStack.v();
        int backpackNum = tagCompound.h(key);
        return backpackNum;
    }

    public static String getString(ItemStack item, String key){
        net.minecraft.world.item.ItemStack itemStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tagCompound = itemStack.v();
        String backpackNum = tagCompound.l(key);
        return backpackNum;
    }

    public static void resetBackpack(Player p){
        if(Backpacks.getBackpacksManager().getBackpacks().containsKey(p.getUniqueId().toString())){
            Backpack backpack = Backpacks.getBackpacksManager().getBackpacks().get(p.getUniqueId().toString());
            backpack.getInventory().clear();
        }
        String uuid = p.getUniqueId().toString();
        for(File file : Backpacks.getBackpacksManager().getInventoriesFolder().listFiles()){
            if(file.getName().contains(uuid)){
                file.delete();
            }
        }
    }

    public static void saveAllBackpacks(){
        for(Player p : Backpacks.getPlugin().getServer().getOnlinePlayers()){
            if(Util.playerHasInventoryFile(p)){
                Util.saveBackpack(p);
            }
        }
    }

    public static void printToLogger(String s){
        Backpacks.getPlugin().getLogger().info(s);
    }

    public static List<String> formatLore(List<String> lore){
        List<String> n = new ArrayList<>();
        for(String s : lore){
            n.add(format(s));
        }
        return n;
    }

    public static ItemStack getBackpackItemStack(){
        Material mat = Material.getMaterial(Backpacks.getPlugin().getConfig().getString("backpack.material"));
        ItemStack bp = new ItemStack(mat, 1);
        ItemMeta meta = bp.getItemMeta();
        meta.setDisplayName(format(Backpacks.getPlugin().getConfig().getString("backpack.display_name")));
        List<String> formattedLore = formatLore(Backpacks.getPlugin().getConfig().getStringList("backpack.lore"));
        meta.setLore(formattedLore);
        bp.setItemMeta(meta);
        return bp;
    }
    public static void loadBackpackRecipe(){
        NamespacedKey key = new NamespacedKey(Backpacks.getPlugin(), "backpack");
        ShapedRecipe backpack = new ShapedRecipe(key, getBackpackItemStack());
        backpack.shape("***", "%$%", "***");
        backpack.setIngredient('*', Material.LEATHER);
        backpack.setIngredient('%', Material.STICK);
        backpack.setIngredient('$', Material.CHEST);
        Backpacks.getPlugin().getServer().addRecipe(backpack);
    }

    public static boolean isBackpackItem(ItemStack i){
        if(i.hasItemMeta() && i.getItemMeta().hasLore()){
            return hasKey(i, "backpack");
        }
        return false;
    }

    public static void saveBackpack(Player p){
        File inventoriesFolder = Backpacks.getBackpacksManager().getInventoriesFolder();
        for(File file : inventoriesFolder.listFiles()){
            if(file.getName().contains(p.getUniqueId().toString())){

                //reset config
                File holder = file;
                file.delete();
                File newFile = new File(Backpacks.getBackpacksManager().getInventoriesFolder(), holder.getName());
                if(!newFile.exists()){
                    try {
                        newFile.createNewFile();
                    } catch (IOException err) {
                        Backpacks.getPlugin().getLogger().info(ChatColor.RED + "Failed to save yml config!");
                    }
                }

                FileConfiguration cfg = YamlConfiguration.loadConfiguration(newFile);

                //save backpack inventory
                cfg.createSection("inventory");
                Backpack backpack = Backpacks.getBackpacksManager().getBackpacks().get(newFile.getName().replace(".yml", ""));
                //save the backpack inventory
                if (backpack == null){
                    Backpacks.getPlugin().getLogger().info(ChatColor.RED + "Backpack is null");
                    return;
                }

                Inventory inv = backpack.getInventory();
                int counter = cfg.getConfigurationSection("inventory").getKeys(false).size();
                for (ItemStack i : inv.getContents()) {
                    if (i != null && !(i.getType().equals(Material.AIR))) {
                        if (Util.isBackpackItem(i)) continue;
                        cfg.getConfigurationSection("inventory").set(Integer.toString(counter), i);
                        counter++;
                    }
                }
                try {
                    cfg.save(newFile);
                } catch (IOException err) {
                    Backpacks.getPlugin().getLogger().info(ChatColor.RED + "Failed to save yml config!");
                }
            }
        }
    }

    public static boolean playerHasInventoryFile(Player p){
        File inventoriesFolder = Backpacks.getBackpacksManager().getInventoriesFolder();
        try{
            for(File file : inventoriesFolder.listFiles()) {
                if (file.getName().contains(p.getUniqueId().toString())) {
                    return true;
                }
            }
        }catch(Exception e){
            Backpacks.getPlugin().getLogger().info("No inventories loaded!");
        }

        return false;
    }

    public static String format(String format) {
        return ChatColor.translateAlternateColorCodes('&', format);
    }
}
