package me.petaenderchest;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.EnderChest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class ClickEvent implements CommandExecutor, Listener {

    private final HashMap<Player, ItemStack[]> ec1 = new HashMap<>();
    private final HashMap<Player, ItemStack[]> ec2 = new HashMap<>();
    private final HashMap<Player, ItemStack[]> ec3 = new HashMap<>();
    private final HashMap<Player, ItemStack[]> ec4 = new HashMap<>();

    private final DataHandler dataHandler;

    public ClickEvent(JavaPlugin plugin) {
        dataHandler = new DataHandler(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Komenda dostępna tylko dla graczy!");
            return true;
        }

        Player player = (Player) sender;
        if (player.hasPermission("petaenderchest.open")) {
            gui(player);
            player.playSound(player.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 10000000, 1);
        } else {
            player.sendMessage("§cBrak uprawnień do tej komendy! (petaenderchest.open)");
        }
        return true;
    }

    // GUI
    public static void gui(Player player) {
        Inventory enderchestGUI = Bukkit.createInventory(null, 27, "§8Enderchesty");
        ItemStack ec1 = createEnderChestItem("Ender Chest 1", "§7EnderChest §3nr.1", "§7Wymagana ranga: §3Brak");
        ItemStack ec2 = createEnderChestItem("Ender Chest 2", "§7EnderChest §3nr.2", "§7Wymagana ranga: §3VIP");
        ItemStack ec3 = createEnderChestItem("Ender Chest 3", "§7EnderChest §3nr.3", "§7Wymagana ranga: §3SVIP");
        ItemStack ec4 = createEnderChestItem("Ender Chest 4", "§7EnderChest §3nr.4", "§7Wymagana ranga: §3MVIP");

        enderchestGUI.setItem(10, ec1);
        enderchestGUI.setItem(12, ec2);
        enderchestGUI.setItem(14, ec3);
        enderchestGUI.setItem(16, ec4);

        player.openInventory(enderchestGUI);
    }

    private static ItemStack createEnderChestItem(String displayName, String lore1, String lore2) {
        ItemStack ec = new ItemStack(Material.ENDER_CHEST);
        ItemMeta ecMeta = ec.getItemMeta();
        ecMeta.setDisplayName(displayName);
        List<String> ecLore = new ArrayList<>();
        ecLore.add(lore1);
        ecLore.add(lore2);
        ecMeta.setLore(ecLore);
        ec.setItemMeta(ecMeta);
        return ec;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        ItemStack clickedItem = e.getCurrentItem();

        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        if (e.getView().getTitle().equals("§8Enderchesty")) {
            e.setCancelled(true);

            if (clickedItem.getItemMeta() == null || clickedItem.getItemMeta().getDisplayName() == null) {
                return;
            }

            if (clickedItem.getItemMeta().getDisplayName().equalsIgnoreCase("Ender Chest 1")) {
                e.setCancelled(true);
                openEnderChestWithItems(player, "ec1", "Ender Chest 1");
            } else if (clickedItem.getItemMeta().getDisplayName().equalsIgnoreCase("Ender Chest 2")) {
                e.setCancelled(true);
                openEnderChestWithItems(player, "ec2", "Ender Chest 2");
            } else if (clickedItem.getItemMeta().getDisplayName().equalsIgnoreCase("Ender Chest 3")) {
                e.setCancelled(true);
                openEnderChestWithItems(player, "ec3", "Ender Chest 3");
            } else if (clickedItem.getItemMeta().getDisplayName().equalsIgnoreCase("Ender Chest 4")) {
                e.setCancelled(true);
                openEnderChestWithItems(player, "ec4", "Ender Chest 4");
            }
        }
    }

    private void openEnderChestWithItems(Player player, String ecKey, String ecDisplayName) {
        UUID playerUUID = player.getUniqueId();

        int ecNumber;
        try {
            ecNumber = Integer.parseInt(ecKey.replaceAll("[^0-4]", ""));
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }

        ItemStack[] items = dataHandler.loadEnderChestData(ecKey, playerUUID);

        if (items == null) {
            items = new ItemStack[27];
            player.sendMessage("§cBłąd: Nie udało się wczytać zawartości Ender Chest dla gracza " + player.getName() + ". Skontaktuj się z administratorem.");
        }

        HashMap<Player, ItemStack[]> ecMap = getEnderChestMap(ecNumber);
        ecMap.put(player, items);

        Inventory ecInventory = Bukkit.createInventory(null, 27, ecDisplayName);
        ecInventory.setContents(items);
        player.openInventory(ecInventory);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        String name = event.getView().getTitle();

        if (name != null && name.startsWith("Ender Chest ")) {
            int ecNumber = Integer.parseInt(name.split(" ")[2]);
            HashMap<Player, ItemStack[]> ecMap = getEnderChestMap(ecNumber);

            if (ecMap != null) {
                ItemStack[] ecItems = event.getInventory().getContents().clone();
                ecMap.put(player, ecItems);
                player.sendMessage("Zamknąłeś skrzynię Ender!");

                saveEnderChestData("ec" + ecNumber, ecMap);
            }
        }
    }


    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        String name = event.getView().getTitle();

        if (name != null && name.startsWith("Ender Chest ")) {
            int ecNumber = Integer.parseInt(name.split(" ")[2]);
            HashMap<Player, ItemStack[]> ecMap = getEnderChestMap(ecNumber);

            if (ecMap != null) {
                loadEnderChestItems(player, ecNumber, event.getInventory());
            }
        }
    }

    private void loadEnderChestItems(Player player, int ecNumber, Inventory inventory) {
        UUID playerUUID = player.getUniqueId();
        String key = "ec" + ecNumber;

        ItemStack[] items = loadEnderChestData(key, playerUUID);

        if (items != null) {
            HashMap<Player, ItemStack[]> ecMap = getEnderChestMap(ecNumber);
            ecMap.put(player, items);

            inventory.setContents(items);
        }
    }

    private ItemStack[] loadEnderChestData(String ecKey, UUID playerUUID) {
        if (dataHandler.hasData(ecKey, playerUUID)) {
            return dataHandler.loadEnderChestData(ecKey, playerUUID);
        } else {
            return new ItemStack[27];
        }
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

    private HashMap<Player, ItemStack[]> getEnderChestMap(int ecNumber) {
        switch (ecNumber) {
            case 1:
                return ec1;
            case 2:
                return ec2;
            case 3:
                return ec3;
            case 4:
                return ec4;
            default:
                return null;
        }
    }
}
