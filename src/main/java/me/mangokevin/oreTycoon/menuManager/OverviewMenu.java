package me.mangokevin.oreTycoon.menuManager;


import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.tycoonManagment.tycoonBlockManagement.TycoonManager;
import me.mangokevin.oreTycoon.tycoonManagment.tycoonBlockManagement.TycoonRegistry;
import me.mangokevin.oreTycoon.utility.Console;
import me.mangokevin.oreTycoon.tycoonManagment.*;
import me.mangokevin.oreTycoon.worth.PriceUtility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class OverviewMenu implements MenuInterface{

    private final OreTycoon plugin;
    private final TycoonManager tycoonManager;
    private final TycoonRegistry tycoonRegistry;
    private final MenuManager menuManager;
    private final int page;


    public OverviewMenu(OreTycoon plugin, int page) {
        this.plugin = plugin;
        this.tycoonManager = plugin.getTycoonManager();
        this.tycoonRegistry = plugin.getTycoonRegistry();
        this.menuManager = plugin.getMenuManager();
        this.page = page;

    }

    @Override
    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(new TycoonHolder(this), 54, ChatColor.DARK_GRAY + "Tycoon Overview");
        refresh(player, inventory);
        player.openInventory(inventory);
    }
    @Override
    public void refresh(Player player, Inventory inventory) {
        List<TycoonBlock> tycoonBlockList = tycoonRegistry.getAllTycoonsFromPlayer(player.getUniqueId());

        int maxTycoonsPerPlayer = tycoonManager.getMaxTycoonsPerPlayer();

        MenuManager.addFiller(inventory, Material.GRAY_STAINED_GLASS_PANE);
        boolean toggleAll = true;
        boolean toggleAllAutoMiner;
        int startIndex = page * 14;
        for (int i = 0; i < 14; i++) {
            int idx = startIndex + i;
            if (idx < tycoonBlockList.size()) {
                if (!tycoonBlockList.get(idx).isActive()) {
                    toggleAll = false;
                    break;
                }
            }
        }
        int autoMinerEnabled = 0;
        int autoMinerDisabled = 0;
        for (int i = 0; i < getUsableSlots().size(); i++) {
            int idx = startIndex + i;
            if (idx >= tycoonBlockList.size()) break;
            if (tycoonBlockList.get(idx).isAutoMinerEnabled()) {
                autoMinerEnabled++;
            } else {
                autoMinerDisabled++;
            }
        }
        toggleAllAutoMiner = autoMinerEnabled >= autoMinerDisabled;

        List<Integer> usableSlots = getUsableSlots();
        int itemsPerPage = usableSlots.size(); // 14
        startIndex = page * itemsPerPage;

        // Fill tycoons and emtpy Slots
        for (int i = 0; i < itemsPerPage; i++) {
            int tycoonIndex = startIndex + i;
            int slot = usableSlots.get(i);

            if (tycoonIndex < tycoonBlockList.size()) {
                TycoonBlock block = tycoonBlockList.get(tycoonIndex);
                inventory.setItem(slot, menuManager.createTycoonItem(block));
            } else if (tycoonIndex < maxTycoonsPerPlayer) {
                inventory.setItem(slot, MenuManager.createItemstack(
                        Material.BLACK_STAINED_GLASS_PANE,
                        1,
                        "§8Free Slot",
                        null,
                        false,
                        true));
            }
        }

        // Navigation (PDC-Keys nutzen)
        if (page > 0) {
            ItemStack prevPage = MenuManager.createItemstack(
                    Material.ARROW,
                    1,
                    ChatColor.GOLD + "§e<- Page " + page,
                    null,
                    false,
                    false,
                    true,
                    "page_prev");
            inventory.setItem(45, prevPage);
        }
        if (startIndex + itemsPerPage < maxTycoonsPerPlayer) {
            ItemStack nextPage = MenuManager.createItemstack(
                    Material.ARROW,
                    1,
                    ChatColor.GOLD + "§ePage " + (page + 2) + " ->" ,
                    null,
                    false,
                    false,
                    true,
                    "page_next");
            inventory.setItem(53, nextPage);
        }
        ItemStack item;
        if (toggleAll) {
            List<String> allTycoonSpawnsOffLore = Arrays.asList(
                    "§8§m-----------------------",
                    ChatColor.YELLOW + "Page: " + (page + 1),
                    ChatColor.YELLOW + "[ Click to disable all Tycoons ]",
                    "§8§m-----------------------");
            item = MenuManager.createItemstack(
                    Material.RED_STAINED_GLASS_PANE,
                    1,
                    ChatColor.RED + "Turn off #" + (startIndex + 1) + " - #" + (startIndex + itemsPerPage),
                    allTycoonSpawnsOffLore,
                    false,
                    true,
                    true,
                    "toggle_all_off"
            );
        } else {
            List<String> allTycoonSpawnsOnLore = Arrays.asList(
                    "§8§m-----------------------",
                    ChatColor.YELLOW + "Page: " + (page + 1),
                    ChatColor.YELLOW + "[ Click to enable all Tycoons ]",
                    "§8§m-----------------------");
            item = MenuManager.createItemstack(
                    Material.LIME_STAINED_GLASS_PANE,
                    1,
                    ChatColor.GREEN + "Turn on #" + (startIndex + 1) + " - #" + (startIndex + itemsPerPage),
                    allTycoonSpawnsOnLore,
                    false,
                    true,
                    true,
                    "toggle_all_on"
            );
        }
        ItemStack toggleAutoMinerItem;
        if (toggleAllAutoMiner) {
            List<String> allAutoMinerEnabledLore = Arrays.asList(
                    "§8§m-------------------------------------",
                    ChatColor.YELLOW + "[ Click to disable All Auto Miners ]",
                    "§8§m-------------------------------------");
            toggleAutoMinerItem = MenuManager.createItemstack(
                    Material.IRON_PICKAXE,
                    1,
                    ChatColor.GREEN + "Auto Miners #" + (startIndex + 1) + " - #" + (startIndex + itemsPerPage) + " enabled",
                    allAutoMinerEnabledLore,
                    true,
                    true,
                    true,
                    "autoMiner_enabled"
            );
        } else {
            List<String> allAutoMinerDisabledLore = Arrays.asList(
                    "§8§m-----------------------",
                    ChatColor.YELLOW + "[ Click to enable All Auto Miners ]",
                    "§8§m-----------------------");
            toggleAutoMinerItem = MenuManager.createItemstack(
                    Material.IRON_PICKAXE,
                    1,
                    ChatColor.RED + "Auto Miners #" + (startIndex + 1) + " - #" + (startIndex + itemsPerPage) + " disabled",
                    allAutoMinerDisabledLore,
                    false,
                    true,
                    true,
                    "autoMiner_disabled");
        }
        ItemStack collectAllBalance = MenuManager.createItemstack(
                Material.LIME_BUNDLE,
                1,
                ChatColor.GREEN + "Sell all Tycoon inventories for: " + PriceUtility.formatMoney(getAllWorth(player)),
                null,
                false,
                true,
                true,
                "sell_all"
        );
        inventory.setItem(47, toggleAutoMinerItem);
        inventory.setItem(49, item);
        inventory.setItem(51, collectAllBalance);
    }

    @Override
    public void handleAction(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        List<TycoonBlock> allPlayerTycoons = tycoonRegistry.getAllTycoonsFromPlayer(player.getUniqueId());
        int startIndex = page * getUsableSlots().size();

        if (pdc.has(TycoonData.TYCOON_MENU_ITEM_UID_KEY, PersistentDataType.STRING)) {
            String blockUIDStr = pdc.get(TycoonData.TYCOON_MENU_ITEM_UID_KEY, PersistentDataType.STRING);
            TycoonBlock block = tycoonRegistry.getTycoonBlock(blockUIDStr);
            if (block != null) {
                new StatsMenu(block, plugin).open(player);
            }
            return;
        }
        if (pdc.has(TycoonData.MENU_ACTION_KEY, PersistentDataType.STRING)) {
            String action = pdc.get(TycoonData.MENU_ACTION_KEY, PersistentDataType.STRING);
            switch (action) {
                case "toggle_all_on":
                    doForTycoonsOnPage(allPlayerTycoons, tycoonBlock -> tycoonBlock.setActiveByPlayer(true));
                    break;
                case "toggle_all_off":
                    doForTycoonsOnPage(allPlayerTycoons, tycoonBlock -> tycoonBlock.setActiveByPlayer(false));
                    break;
                case "autoMiner_enabled":
                    doForTycoonsOnPage(allPlayerTycoons, tycoonBlock -> tycoonBlock.setAutoMinerEnabled(false));
                    break;
                case "autoMiner_disabled":
                    doForTycoonsOnPage(allPlayerTycoons, tycoonBlock -> tycoonBlock.setAutoMinerEnabled(true));
                    break;
                case "sell_all":
                    collectAllBalance(player);
                    break;
                case "page_prev":
                    new OverviewMenu(plugin, page - 1).open(player);
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.2f);
                    break;
                case "page_next":
                    new OverviewMenu(plugin, page + 1).open(player);
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.2f);
                    break;
                case null, default:
                    Console.log("[OverviewMenu] Invalid action");
                    break;
            }
            refresh(player, inventory);
        }

    }

    private void collectAllBalance(Player p) {
        List<TycoonBlock> allTycoons = tycoonRegistry.getAllTycoonsFromPlayer(p.getUniqueId());
        List<Integer> usableSlots = getUsableSlots();
        int startIndex = this.page * usableSlots.size();


        // Nur die Tycoons dieser Seite bearbeiten
        double totalWorth = 0;
        for (int i = 0; i < usableSlots.size(); i++) {
            int tycoonIndex = startIndex + i;
            if (tycoonIndex >= allTycoons.size()) break;

            TycoonBlock tycoonBlock = allTycoons.get(tycoonIndex);
            totalWorth += tycoonBlock.sellInventory(tycoonBlock.getInventory(), p);
        }
        p.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "--------------------------");
        p.sendMessage(ChatColor.GREEN + "Total Amount: " + PriceUtility.formatMoney(totalWorth));
        p.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "--------------------------");
        this.open(p);
    }
    private void doForTycoonsOnPage(List<TycoonBlock> playerTycoons, Consumer<TycoonBlock> consumer) {
        List<Integer> usableSlots = getUsableSlots();
        int startIndex = this.page * usableSlots.size();

        for (int i = 0; i < getUsableSlots().size(); i++) {
            int tycoonIndex = startIndex + i;
            if (tycoonIndex >= playerTycoons.size()) break;

            TycoonBlock tycoonBlock = playerTycoons.get(tycoonIndex);
            consumer.accept(tycoonBlock);
        }
    }
    private double getAllWorth(Player p){
        List<TycoonBlock> allTycoons = tycoonRegistry.getAllTycoonsFromPlayer(p.getUniqueId());
        List<Integer> usableSlots = getUsableSlots();
        int startIndex = this.page * usableSlots.size();

        // Bestimmen, ob wir ein- oder ausschalten (basierend auf dem aktuellen Button)

        double totalWorth = 0;
        // Nur die Tycoons dieser Seite bearbeiten
        for (int i = 0; i < usableSlots.size(); i++) {
            int tycoonIndex = startIndex + i;
            if (tycoonIndex >= allTycoons.size()) break;

            TycoonBlock tycoonBlock = allTycoons.get(tycoonIndex);
            totalWorth += PriceUtility.calculateWorth(tycoonBlock.getInventory());
        }
        // Das Menü komplett neu laden, um alle Items (Tycoons + Button) zu aktualisieren
        return totalWorth;
    }
    private void toggleTycoonsAutoMiner(boolean toggle, Player p) {
        List<TycoonBlock> allTycoons = tycoonRegistry.getAllTycoonsFromPlayer(p.getUniqueId());
        List<Integer> usableSlots = getUsableSlots();
        int startIndex = this.page * usableSlots.size();

        // Bestimmen, ob wir ein- oder ausschalten (basierend auf dem aktuellen Button)


        // Nur die Tycoons dieser Seite bearbeiten
        for (int i = 0; i < usableSlots.size(); i++) {
            int tycoonIndex = startIndex + i;
            if (tycoonIndex >= allTycoons.size()) break;

            TycoonBlock block = allTycoons.get(tycoonIndex);
            block.setAutoMinerEnabled(toggle);
        }

        // Sound abspielen
        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.6f, toggle ? 1.2f : 0.8f);

        // Das Menü komplett neu laden, um alle Items (Tycoons + Button) zu aktualisieren
        this.open(p);
    }
    private void toggleTycoons(Material clickedItem, Player p) {
        List<TycoonBlock> allTycoons = tycoonRegistry.getAllTycoonsFromPlayer(p.getUniqueId());
        List<Integer> usableSlots = getUsableSlots();
        int startIndex = this.page * usableSlots.size();

        // Bestimmen, ob wir ein- oder ausschalten (basierend auf dem aktuellen Button)
        boolean shouldActivate = clickedItem == Material.LIME_CONCRETE;

        // Nur die Tycoons dieser Seite bearbeiten
        for (int i = 0; i < usableSlots.size(); i++) {
            int tycoonIndex = startIndex + i;
            if (tycoonIndex >= allTycoons.size()) break;

            TycoonBlock block = allTycoons.get(tycoonIndex);
            block.setActiveByPlayer(shouldActivate);
        }

        // Sound abspielen
        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.6f, shouldActivate ? 1.2f : 0.8f);

        // Das Menü komplett neu laden, um alle Items (Tycoons + Button) zu aktualisieren
        this.open(p);

    }
    private List<Integer> getUsableSlots() {
        return Arrays.asList(10, 11, 12, 13, 14, 15, 16, 28, 29, 30, 31, 32, 33, 34);
    }
}
