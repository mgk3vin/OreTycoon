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

import java.util.Arrays;
import java.util.List;

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
        boolean toggleAllAutoMiner = true;
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
        for (int i = 0; i < 14; i++) {
            int idx = startIndex + i;
            if (idx < tycoonBlockList.size()) {
                if (!tycoonBlockList.get(idx).isAutoMinerEnabled()) {
                    if (!tycoonBlockList.get(idx).getTycoonUpgrades().isAutoMinerUnlocked()) {
                        continue;
                    }
                    toggleAllAutoMiner = false;
                    break;
                }
            } else {
                toggleAllAutoMiner = false;
            }
        }
        List<Integer> usableSlots = getUsableSlots();
        int itemsPerPage = usableSlots.size(); // 14
        startIndex = page * itemsPerPage;

        // Tycoons & Leere Slots füllen
        for (int i = 0; i < itemsPerPage; i++) {
            int tycoonIndex = startIndex + i;
            int slot = usableSlots.get(i);

            if (tycoonIndex < tycoonBlockList.size()) {
                TycoonBlock block = tycoonBlockList.get(tycoonIndex);
                inventory.setItem(slot, menuManager.createTycoonItem(block));
            } else if (tycoonIndex < maxTycoonsPerPlayer) {
                inventory.setItem(slot, MenuManager.createItemstack(Material.BLACK_STAINED_GLASS_PANE, 1, "§8Free Slot", null, false, true));
            }
        }

        // Navigation (PDC-Keys nutzen)
        if (page > 0) {
            inventory.setItem(45, createNavArrow("§e<- Page " + page, "page_prev"));
        }
        if (startIndex + itemsPerPage < maxTycoonsPerPlayer) {
            inventory.setItem(53, createNavArrow("§ePage " + (page + 2) + " ->", "page_next"));
        }
        ItemStack item;
        if (toggleAll) {
            item = MenuManager.createItemstack(Material.RED_CONCRETE, 1, ChatColor.RED + "Turn off #" + (startIndex + 1) + " - #" + (startIndex + itemsPerPage), null, false, true);
            ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta == null) return;
            PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
            pdc.set(TycoonData.MENU_ACTION_KEY, PersistentDataType.STRING, "toggle_all_off");
            item.setItemMeta(itemMeta);
        } else {
            item = MenuManager.createItemstack(Material.LIME_CONCRETE, 1, ChatColor.GREEN + "Turn on #" + (startIndex + 1) + " - #" + (startIndex + itemsPerPage), null, false, true);
            ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta == null) return;
            PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
            pdc.set(TycoonData.MENU_ACTION_KEY, PersistentDataType.STRING, "toggle_all_on");
            item.setItemMeta(itemMeta);
        }
        ItemStack toggleAutoMiner;
        if (toggleAllAutoMiner) {
            toggleAutoMiner = MenuManager.createItemstack(Material.IRON_PICKAXE, 1, ChatColor.GREEN + "Auto Miner Enabled", null, true, true);
            ItemMeta itemMeta = toggleAutoMiner.getItemMeta();
            if (itemMeta == null) return;
            PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
            pdc.set(TycoonData.MENU_ACTION_KEY, PersistentDataType.STRING, "autominer_enabled");
            toggleAutoMiner.setItemMeta(itemMeta);
        }else{
            toggleAutoMiner = MenuManager.createItemstack(Material.IRON_PICKAXE, 1, ChatColor.RED + "Auto Miner Disabled", null, false, true);
            ItemMeta itemMeta = toggleAutoMiner.getItemMeta();
            if (itemMeta == null) return;
            PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
            pdc.set(TycoonData.MENU_ACTION_KEY, PersistentDataType.STRING, "autominer_disabled");
            toggleAutoMiner.setItemMeta(itemMeta);
        }
        ItemStack collectAllBalance = MenuManager.createItemstack(Material.GREEN_STAINED_GLASS_PANE, 1, ChatColor.GREEN + "Sell all blocks for: " + PriceUtility.formatMoney(getAllWorth(player)), null, false, true);
        ItemMeta itemMeta = collectAllBalance.getItemMeta();
        if (itemMeta == null) return;
        PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
        pdc.set(TycoonData.MENU_ACTION_KEY, PersistentDataType.STRING, "sell_all");
        collectAllBalance.setItemMeta(itemMeta);
        inventory.setItem(47, toggleAutoMiner);
        inventory.setItem(49, item);
        inventory.setItem(51, collectAllBalance);
    }

    @Override
    public void handleAction(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

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
                case "toggle_all_off", "toggle_all_on":
                    toggleTycoons(item.getType(), player);
                    break;
                case "autominer_enabled":
                    toggleTycoonsAutoMiner(false, player);
                    break;
                case "autominer_disabled":
                    toggleTycoonsAutoMiner(true, player);
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
        }

    }

    // Hilfsmethode für Navigation-Items
    private ItemStack createNavArrow(String name, String action) {
        ItemStack arrow = MenuManager.createItemstack(Material.ARROW, 1, name, null, false, true);
        ItemMeta meta = arrow.getItemMeta();
        meta.getPersistentDataContainer().set(TycoonData.MENU_ACTION_KEY, PersistentDataType.STRING, action);
        arrow.setItemMeta(meta);
        return arrow;
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
