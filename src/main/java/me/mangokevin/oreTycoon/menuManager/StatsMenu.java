package me.mangokevin.oreTycoon.menuManager;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.tycoonManagment.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;

public class StatsMenu implements MenuInterface {
    private final OreTycoon plugin;
    private final TycoonBlock tycoonBlock;
    private final MenuManager menuManager;
    private final TycoonBlockManager blockManager;

    public StatsMenu(TycoonBlock tycoonBlock, OreTycoon plugin) {
        this.plugin = plugin;
        this.tycoonBlock = tycoonBlock;
        this.menuManager = plugin.getMenuManager();
        this.blockManager = plugin.getBlockManager();
    }
    @Override
    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(new TycoonHolder(this), 27, ChatColor.DARK_GRAY + "Tycoon Stats");

        refresh(player, inventory);

        player.openInventory(inventory);
    }
    @Override
    public void refresh(Player player, Inventory inventory) {
        MenuManager.addFiller(inventory, Material.GRAY_STAINED_GLASS_PANE);

        //Tycoon Icon
        ItemStack tycoonItem = menuManager.createTycoonItem(tycoonBlock);
        ItemMeta tycoonMeta = tycoonItem.getItemMeta();
        if (tycoonMeta != null) {
            List<String> lore = tycoonMeta.getLore();
            lore.add(ChatColor.YELLOW + "[Left click to toggle status]");
            lore.add(ChatColor.YELLOW + "[Right click to teleport]");
            tycoonMeta.setLore(lore);
            tycoonItem.setItemMeta(tycoonMeta);
        }
        inventory.setItem(13, tycoonItem);

        //Return to Overviewmenu icon
        inventory.setItem(26, MenuManager.createItemstack(Material.OAK_DOOR,
                1,
                ChatColor.RED + "<- Back to Overview",
                null,
                false,
                true,
                "return"));


        //Autominer icon
        List<String> minerLore = Arrays.asList("§8§m-----------------------",
                ChatColor.GREEN + "~" + PriceUtility.calculateWorthPerHour(tycoonBlock.getMiningRateFormatted(), tycoonBlock.getAverageWorth()) + "/h",
                "§8§m-----------------------");
        if (tycoonBlock.isAutoMinerEnabled()) {
            inventory.setItem(22, MenuManager.createItemstack(Material.IRON_PICKAXE,
                    1,
                    ChatColor.GREEN + "Auto Miner Enabled",
                    minerLore,
                    true,
                    true,
                    "toggle_autominer_off"));
        } else {
            inventory.setItem(22, MenuManager.createItemstack(Material.IRON_PICKAXE,
                    1,
                    ChatColor.RED + "Auto Miner Disabled",
                    minerLore,
                    false,
                    true,
                    "toggle_autominer_on"));
        }

        //Level Path icon
        ItemStack levelPath = MenuManager.createItemstack(Material.NETHER_STAR,
                1,
                ChatColor.AQUA + "Level path",
                null,
                true,
                true,
                "level_path");
        inventory.setItem(20, levelPath);

        //Upgrades Icon
        ItemStack upgrades = MenuManager.createItemstack(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE,
                1,
                ChatColor.AQUA + "Upgrades",
                null, true,
                true,
                "upgrades");
        inventory.setItem(24, upgrades);

        //Inventory Icon
        List<String> inventoryLore = Arrays.asList("§8§m-----------------------",
                ChatColor.WHITE + "Worth: "  + ChatColor.GREEN + PriceUtility.calculateWorthFormatted(tycoonBlock.getInventory()),
                ChatColor.WHITE + "Storage: " + tycoonBlock.getStorageStatisticFormatted() + " items",
                "§8§m-----------------------",
                ChatColor.YELLOW + "[Left click to open]",
                ChatColor.YELLOW + "[Right click to sell]"
                );
        inventory.setItem(18, MenuManager.createItemstack(Material.CHEST_MINECART,
                1,
                ChatColor.GOLD + "Inventory",
                inventoryLore,
                false,
                true,
                "inventory"));


    }

    @Override
    public void handleAction(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();
        ItemStack item = event.getCurrentItem();
        ItemMeta meta = item.getItemMeta();
        ClickType inventoryClick = event.getClick();
        if (meta == null) return;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        String action = pdc.get(TycoonData.MENU_ACTION_KEY, PersistentDataType.STRING);
        if (action == null) return;
        switch (action) {
            case "tycoon_menu_item":
                if (inventoryClick == ClickType.LEFT) {
                    tycoonBlock.setActive(!tycoonBlock.isActive());
                    refresh(player, inventory);
                    if (tycoonBlock.isActive()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 2f, 1.5f);
                    }else {
                        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 2f, 0.6f);
                    }

                }else if (inventoryClick == ClickType.RIGHT) {
                    tycoonBlock.teleportPlayer(player);
                }
                break;
            case "return":
                int itemsPerPage = 14;
                int page;
                if (tycoonBlock.getIndex() >= 0){
                    page = tycoonBlock.getIndex() / itemsPerPage;
                }else{
                    page = 0;
                }
                new OverviewMenu(plugin, page).open(player);
                break;
            case "level_path":
                new TycoonLevelPath(tycoonBlock, 0, plugin).open(player);
                break;
            case "upgrades":
                new TycoonUpgradeMenu(tycoonBlock, plugin).open(player);
                break;
            case "inventory":
                if (inventoryClick == ClickType.LEFT) {
                    new TycoonInventory(tycoonBlock, plugin).open(player);
                }else if (inventoryClick == ClickType.RIGHT) {
                    tycoonBlock.sellInventory(tycoonBlock.getInventory(), player);
                    refresh(player, inventory);
                }
                break;
            case "toggle_autominer_off":
                tycoonBlock.setAutoMinerEnabled(false);
                refresh(player, inventory);
                break;
            case "toggle_autominer_on":
                tycoonBlock.setAutoMinerEnabled(true);
                refresh(player, inventory);
                break;
        }

    }
    public TycoonBlock getTycoonBlock() {
        return tycoonBlock;
    }
}
