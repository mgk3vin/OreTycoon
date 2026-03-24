package me.mangokevin.oreTycoon.menuManager;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.tycoonManagment.*;
import me.mangokevin.oreTycoon.worth.PriceUtility;
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

    public StatsMenu(TycoonBlock tycoonBlock, OreTycoon plugin) {
        this.plugin = plugin;
        this.tycoonBlock = tycoonBlock;
        this.menuManager = plugin.getMenuManager();

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


        //Tycoon Icon slot 13
        ItemStack tycoonItem = menuManager.createTycoonItem(tycoonBlock);
        ItemMeta tycoonMeta = tycoonItem.getItemMeta();
        if (tycoonMeta != null) {
            List<String> lore = tycoonMeta.getLore();
            if (lore != null) {
                lore.add(ChatColor.YELLOW + "[Left click to toggle status]");
                lore.add(ChatColor.YELLOW + "[Right click to teleport]");
                tycoonMeta.setLore(lore);
                tycoonItem.setItemMeta(tycoonMeta);
            }
        }
        inventory.setItem(13, tycoonItem);

        //Inventory Icon slot 18
        List<String> inventoryLore = Arrays.asList("§8§m-----------------------",
                ChatColor.WHITE + "Worth: "  + ChatColor.GREEN + PriceUtility.formatMoney(PriceUtility.calculateWorth(tycoonBlock.getStoredItems())),
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
                true,
                "inventory"));

        //Return to Overviewmenu icon
        inventory.setItem(26, MenuManager.createItemstack(Material.OAK_DOOR,
                1,
                ChatColor.RED + "<- Back to Overview",
                null,
                false,
                true,
                true,
                "return"));

        //Level Path icon slot 20
        ItemStack levelPath = MenuManager.createItemstack(Material.NETHER_STAR,
                1,
                ChatColor.AQUA + "Level path",
                null,
                true,
                true,
                true,
                "level_path");
        inventory.setItem(20, levelPath);

        //Spawn block choice icon slot 21
        List<String> spawnBlocksLore = Arrays.asList(
                "§8§m-----------------------",
                ChatColor.GRAY + "Choose which blocks",
                ChatColor.GRAY + "should be spawned.",
                ChatColor.GRAY + "Unlock more Blocks ",
                ChatColor.GRAY + "worth more Money.",
                "§8§m-----------------------"
        );
        ItemStack spawnBlocksChoice = MenuManager.createItemstack(Material.TRIAL_SPAWNER,
                1,
                ChatColor.GOLD + "Spawn blocks",
                spawnBlocksLore,
                true,
                true,
                true,
                "spawn_blocks");
        inventory.setItem(21, spawnBlocksChoice);

        //Autominer icon slot 22
        if (tycoonBlock.getTycoonUpgrades().isAutoMinerUnlocked()) {
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
                        true,
                        "toggle_autominer_off"));
            } else {
                inventory.setItem(22, MenuManager.createItemstack(Material.IRON_PICKAXE,
                        1,
                        ChatColor.RED + "Auto Miner Disabled",
                        minerLore,
                        false,
                        true,
                        true,
                        "toggle_autominer_on"));
            }
        }else {
            List<String> minerLore = Arrays.asList("§8§m-----------------------",
                    ChatColor.RED + "Unlock at Level 5",
                    "§8§m-----------------------");
            ItemStack autominerLocked = MenuManager.createItemstack(Material.IRON_BARS,
                    1,
                    ChatColor.RED + "Auto Miner",
                    minerLore,
                    true,
                    true,
                    true,
                    "autominer_locked");
            inventory.setItem(22, autominerLocked);
        }
        //Booster Icon slot 23
        if (tycoonBlock.getTycoonBoosterManager().isAutoMinerBoosterActive()) {
            ItemStack autoMinerBooster = tycoonBlock.getAutoMinerSpeedBooster().getItem(1);
            inventory.setItem(23, autoMinerBooster);
        } else if (tycoonBlock.getTycoonBoosterManager().isSellMultiplierBoosterActive()) {
            ItemStack sellMultiplierBooster = tycoonBlock.getSellMultiplierBooster().getItem(1);
            inventory.setItem(23, sellMultiplierBooster);
        }else if (tycoonBlock.getTycoonBoosterManager().isSpawnSpeedBoosterActive()){
            ItemStack spawnSpeedBooster = tycoonBlock.getSpawnSpeedBooster().getItem(1);
            inventory.setItem(23, spawnSpeedBooster);
        }
        else {
            ItemStack autoMinerBooster = MenuManager.createItemstack(
                    Material.NETHERITE_INGOT,
                    1,
                    ChatColor.GRAY + "No active Booster...",
                    null,
                    false,
                    true,
                    true,
                    "tycoon_booster");
            inventory.setItem(23, autoMinerBooster);
        }

        //Upgrades Icon slot 24
        ItemStack upgrades = MenuManager.createItemstack(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE,
                1,
                ChatColor.AQUA + "Upgrades",
                null,
                true,
                true,
                true,
                "upgrades");
        inventory.setItem(24, upgrades);

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
                    tycoonBlock.setActiveByPlayer(!tycoonBlock.isActive());
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
                    page = tycoonBlock.getIndex() / (itemsPerPage + 1);//+1 for item 15 on the next page
                }else{
                    page = 0;
                }
                new OverviewMenu(plugin, page).open(player);
                break;
            case "level_path":
                int tycoonLevel = tycoonBlock.getLevel();
                int levelPage;
                if (tycoonBlock.getIndex() >= 0){
                    levelPage = tycoonLevel / 21; //21 Hardcode for 21 levels per page in levelsmenu
                }else{
                    levelPage = 0;
                }
                new TycoonLevelPath(tycoonBlock, levelPage, plugin).open(player);
                break;
            case "upgrades":
                new TycoonUpgradeMenu(tycoonBlock, plugin).open(player);
                break;
            case "tycoon_booster", "tycoon_booster_item":
                new TycoonBoosterMenu(tycoonBlock, plugin).open(player);
                break;
            case "inventory":
                if (inventoryClick == ClickType.LEFT) {
                    new TycoonInventory(plugin, tycoonBlock, 0).open(player);
                }else if (inventoryClick == ClickType.RIGHT) {
                    //tycoonBlock.sellInventory(tycoonBlock.getDisplayInventory(), player);
                    tycoonBlock.sellTycoonInventory(player);
                    refresh(player, inventory);
                }
                break;
            case "spawn_blocks":
                new TycoonSpawnBlocksMenu(tycoonBlock, plugin).open(player);
                break;
            case "toggle_autominer_off":
                tycoonBlock.setAutoMinerEnabled(false);
                refresh(player, inventory);
                break;
            case "toggle_autominer_on":
                tycoonBlock.setAutoMinerEnabled(true);
                refresh(player, inventory);
                break;
            case "autominer_locked":
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1f, 0.8f);
                break;
        }

    }
    public TycoonBlock getTycoonBlock() {
        return tycoonBlock;
    }
}
