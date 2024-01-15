package de.harrisblog.backpacks;

import de.harrisblog.backpacks.commands.BackpackCommand;
import de.harrisblog.backpacks.commands.BackpackCommandCompleter;
import de.harrisblog.backpacks.listeners.*;
import de.harrisblog.backpacks.managers.BackpacksManager;
import de.harrisblog.backpacks.managers.DeadBackpacksManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class Backpacks extends JavaPlugin {
    //TODO

    private static BackpacksManager backpacksManager;
    private static DeadBackpacksManager deadBackpacksManager;
    private static Plugin plugin;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        plugin = this;
        getCommand("bp").setExecutor(new BackpackCommand());
        getCommand("bp").setTabCompleter(new BackpackCommandCompleter());

        Util.loadBackpackRecipe();
        backpacksManager = new BackpacksManager();
        deadBackpacksManager = new DeadBackpacksManager();
        deadBackpacksManager.spawnDeadBackpacks();
        registerListeners();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        DeadBackpacksManager cleanupManager = new DeadBackpacksManager();
        cleanupManager.clearAllDeadBackpacks();
        Util.saveAllBackpacks();
    }

    public void registerListeners(){
        plugin.getServer().getPluginManager().registerEvents(new CraftListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new BackpackPlaceListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new InteractListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new BackpackNestListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new DeathEvent(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new DeadBackpackInteract(), plugin);
    }

    public static Plugin getPlugin() {
        return plugin;
    }

    public static DeadBackpacksManager getDeadBackpacksManager() {
        return deadBackpacksManager;
    }

    public static BackpacksManager getBackpacksManager() {
        return backpacksManager;
    }
}
