package me.petaenderchest;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class DataHandler {

    private final JavaPlugin plugin;
    private final FileConfiguration dataConfig;
    private final File dataFile;

    public DataHandler(JavaPlugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "data.yml");
        this.dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        createDataFile();
    }

    public void saveEnderChestData(String key, UUID playerUUID, ItemStack[] items) {
        dataConfig.set(key + "." + playerUUID.toString(), itemsToList(items));
        saveDataConfig();
    }

    public ItemStack[] loadEnderChestData(String key, UUID playerUUID) {
        if (dataConfig.contains(key + "." + playerUUID.toString())) {
            return ((List<ItemStack>) dataConfig.get(key + "." + playerUUID.toString())).toArray(new ItemStack[0]);
        }
        return null;
    }

    public ItemStack[] loadData(String key, UUID playerUUID) {
        if (dataConfig.contains(key + "." + playerUUID.toString())) {
            List<?> itemList = dataConfig.getList(key + "." + playerUUID);
            if (itemList != null) {
                return itemList.toArray(new ItemStack[0]);
            }
        }
        return null;
    }

    public boolean hasData(String key, UUID playerUUID) {
        return dataConfig.contains(key + "." + playerUUID.toString());
    }

    private List<?> itemsToList(ItemStack[] items) {
        return Arrays.asList(items);
    }


    private void createDataFile() {
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveDataConfig() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
