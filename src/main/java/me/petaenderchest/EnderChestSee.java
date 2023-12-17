package me.petaenderchest;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EnderChestSee implements CommandExecutor, Listener {

    private final DataHandler dataHandler;

    private final InventoryHandler inventoryHandler;

    public EnderChestSee(JavaPlugin plugin) {
        this.dataHandler = new DataHandler(plugin);
        this.inventoryHandler = new InventoryHandler(plugin);
    }
    private final HashMap<Player, ItemStack[]> ec1 = new HashMap<>();
    private final HashMap<Player, ItemStack[]> ec2 = new HashMap<>();
    private final HashMap<Player, ItemStack[]> ec3 = new HashMap<>();
    private final HashMap<Player, ItemStack[]> ec4 = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Ta komenda jest dostepna tylko dla graczy!");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("petaenderchest.see.other") && !player.hasPermission("petaenderchest.admin")) {
            player.sendMessage("§cNie posiadasz permisji do uzycia tej komendy!");
            return true;
        }

        if (args.length != 2) {
            player.sendMessage("§cPoprawne uzycie: /ecsee <gracz> <numer enderchesta>");
            return true;
        }

        String targetName = args[0];
        int ecNumber;

        try {
            ecNumber = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage("Numer EnderChesta musi byc liczba całkowitą!");
            return true;
        }

        Player targetPlayer = Bukkit.getPlayer(targetName);

        if (targetPlayer == null || !targetPlayer.isOnline()) {
            player.sendMessage("§cPodany gracz nie istnieje bądż jest offline!");
            return true;
        }

        if (ecNumber > 4 || ecNumber < 0) {
            player.sendMessage("§cNumer enderchesta musi byc w zakresie 0 - 4!");
            return true;
        }

        UUID targetPlayerUUID = targetPlayer.getUniqueId();
        String ecKey = "ec" + ecNumber;

        if (!dataHandler.hasData(ecKey, targetPlayerUUID)) {
            player.sendMessage("§cBrak danych dla enderchesta " + ecNumber + " dla gracza " + targetPlayer.getName());
            return true;
        }
        ItemStack[] itemy = dataHandler.loadEnderChestData(ecKey, targetPlayerUUID);
        Inventory someoneEnderChest = Bukkit.createInventory(null, 27, "§cEnderChest numer " + ecNumber + " gracza " + targetName);
        someoneEnderChest.setContents(itemy);
        player.openInventory(someoneEnderChest); 
        return true;
    }

    private void openEnderChestOther(Player player, String targetName, int ecNumber) {
        Bukkit.getLogger().info("Próba otwarcia EnderChest gracza: " + targetName + ", numer: " + ecNumber);

        String ecKey = "ec" + ecNumber;

        Inventory ecInventory = Bukkit.createInventory(null, 27, "§cEnderChest numer " + ecNumber + " gracza " + targetName);

        if (ecInventory == null) {
            Bukkit.getLogger().warning("Błąd: EnderChest Inventory jest nullem!");
            player.sendMessage("§cBłąd: EnderChest Inventory jest nullem!");
            return;
        }


        Player targetPlayer = Bukkit.getPlayer(targetName);

        HashMap<Player, ItemStack[]> ecMap = getEnderChestMap(ecNumber);

        ItemStack[] items = dataHandler.loadEnderChestData(ecKey, targetPlayer.getUniqueId());

        ecInventory.setContents(items);

        inventoryHandler.loadEnderChestData(ecKey, ecMap);

        dataHandler.saveEnderChestData(ecKey, targetPlayer.getUniqueId(), items);

        player.openInventory(ecInventory);

        Bukkit.getLogger().info("EnderChest otwarty pomyślnie!");


    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();

        if (clickedInventory == null) {
            return;
        }

        String inventoryTitle = event.getView().getTitle();

        if (inventoryTitle != null && inventoryTitle.startsWith("§cEnderChest numer")) {

            String[] titleParts = inventoryTitle.split(" ");
            if (titleParts.length >= 4) {
                String targetName = titleParts[4];
                int ecNumber;

                try {
                    ecNumber = Integer.parseInt(titleParts[2]);
                } catch (NumberFormatException e) {
                    throw new RuntimeException(e);
                }
                HashMap<Player, ItemStack[]> ecMap = getEnderChestMap(ecNumber);
                UUID targetPlayerUUID = Bukkit.getOfflinePlayer(targetName).getUniqueId();
                String ecKey = "ec" + ecNumber;

                if (ecKey == null || targetPlayerUUID == null) {
                    return;
                }
                    ItemStack[] items = clickedInventory.getContents();
                    saveEnderChestData(ecKey, ecMap);
                    player.sendMessage("§aZapisano zmiany w Ender Chest!");
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        System.out.println(0);
        Player player = (Player) event.getPlayer();
        Inventory closedInventory = event.getView().getTopInventory();

        if (closedInventory == null) {

            return;
        }

        String inventoryTitle = event.getView().getTitle();

        if (inventoryTitle != null && inventoryTitle.startsWith("§cEnderChest numer")) {
            System.out.println(1);

            String[] titleParts = inventoryTitle.split(" ");

            if (titleParts.length > 4) {
                System.out.println(2);
                String targetName = titleParts[4];
                System.out.println("Gracz " +targetName);
                int ecNumber;
                try {
                    System.out.println(3);
                    ecNumber = Integer.parseInt(titleParts[2]);
                    System.out.println(3.5);
                } catch (NumberFormatException e) {
                    System.out.println(4);
                    throw new RuntimeException(e);
                }

                System.out.println("Numer " + ecNumber);
                UUID targetPlayerUUID = Bukkit.getOfflinePlayer(targetName).getUniqueId();
                HashMap<Player, ItemStack[]> ecMap = getEnderChestMap(ecNumber);
                System.out.println(targetPlayerUUID);
                String ecKey = "ec" + ecNumber;
                System.out.println(ecKey);

                if (ecKey == null || targetPlayerUUID == null) {
                    System.out.println(5);
                    return;
                }
                System.out.println(6);
                saveEnderChestData(ecKey, ecMap);
                System.out.println(7);
            }
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
