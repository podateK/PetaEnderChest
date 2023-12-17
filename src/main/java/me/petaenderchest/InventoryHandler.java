package me.petaenderchest;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InventoryHandler {

    private final HashMap<Player, ItemStack[]> ec1 = new HashMap<>();
    private final HashMap<Player, ItemStack[]> ec2 = new HashMap<>();
    private final HashMap<Player, ItemStack[]> ec3 = new HashMap<>();
    private final HashMap<Player, ItemStack[]> ec4 = new HashMap<>();

    private final DataHandler dataHandler;

    public InventoryHandler(JavaPlugin plugin) {
        dataHandler = new DataHandler(plugin);
    }

    public void saveData() {
        saveEnderChestData("ec1", ec1);
        saveEnderChestData("ec2", ec2);
        saveEnderChestData("ec3", ec3);
        saveEnderChestData("ec4", ec4);
    }

    public void loadData() {
        ec1.clear();
        ec2.clear();
        ec3.clear();
        ec4.clear();

        loadEnderChestData("ec1", ec1);
        loadEnderChestData("ec2", ec2);
        loadEnderChestData("ec3", ec3);
        loadEnderChestData("ec4", ec4);
    }

    private void saveEnderChestData(String key, HashMap<Player, ItemStack[]> ecMap) {
        for (Map.Entry<Player, ItemStack[]> entry : ecMap.entrySet()) {
            Player player = entry.getKey();
            ItemStack[] items = entry.getValue();

            if (player != null && items != null) {
                UUID playerUUID = player.getUniqueId();
                dataHandler.saveEnderChestData(key, playerUUID, items);
            }
        }
    }

    public void loadEnderChestData(String key, HashMap<Player, ItemStack[]> ecMap) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID playerUUID = player.getUniqueId();
            ItemStack[] items = dataHandler.loadData(key, playerUUID);

            if (items != null) {
                ecMap.put(player, items);
            }
        }
    }
}
