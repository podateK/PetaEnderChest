package me.petaenderchest;


import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.units.qual.C;

import java.io.File;
import java.util.UUID;

public final class PetaEnderChest extends JavaPlugin implements Listener {

    private static PetaEnderChest instance;
    private static DataHandler dataHandler;
    private InventoryHandler inventoryHandler;


    private void sender(String message) {
        Bukkit.getConsoleSender().sendMessage(message);
    }
    @Override
    public void onEnable() {
        instance = this;
        File pluginFolder = getDataFolder();
        if (!pluginFolder.exists()) {
            pluginFolder.mkdir();
        }
        getServer().getPluginManager().registerEvents(new ClickEvent(instance), this);
        getServer().getPluginManager().registerEvents(new EnderChestSee(instance), this);
        getServer().getPluginManager().registerEvents(new NormalEnderChestListener(), this);
        getCommand("ec").setExecutor(new ClickEvent(instance));
        getCommand("ecsee").setExecutor(new EnderChestSee(instance));

        inventoryHandler = new InventoryHandler(this);
        dataHandler = new DataHandler(this);
        sender("PetaEnderChest - Turning on");
        sender("Made with love by podateK_");
        sender("Any bugs please report in ticket on  discord server moderncode.eu");
    }



    @Override
    public void onDisable() {
        if (inventoryHandler != null) {
         inventoryHandler.saveData();
        }
        sender("PetaEnderChest - Turning off");
        sender("Made with love by podateK_");
        sender("Any bugs please report in ticket on  discord server moderncode.eu");
    }


    public static PetaEnderChest getInstance() {
        return instance;
    }

    public static DataHandler getDataHandler() {
        return dataHandler;
    }
}
