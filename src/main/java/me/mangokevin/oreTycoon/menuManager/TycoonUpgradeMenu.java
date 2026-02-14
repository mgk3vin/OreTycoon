package me.mangokevin.oreTycoon.menuManager;

import me.mangokevin.oreTycoon.OreTycoon;
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


public class TycoonUpgradeMenu implements MenuInterface{
    private final TycoonBlock tycoonBlock;
    private final OreTycoon plugin;
    public TycoonUpgradeMenu(TycoonBlock tycoonBlock, OreTycoon plugin) {
        this.tycoonBlock = tycoonBlock;
        this.plugin = plugin;
    }

    @Override
    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(new TycoonHolder(this), 45, "Upgrade Menu");
        refresh(player,inventory);
        player.openInventory(inventory);
    }

    @Override
    public void refresh(Player player, Inventory inventory) {
        TycoonUpgrades tycoonUpgrades = tycoonBlock.getTycoonUpgrades();
        MenuManager.addFiller(inventory, Material.ORANGE_STAINED_GLASS_PANE);
        //  20 | 22 | 24
        //<editor-fold desc="⚒️ Spawn rate Upgrade">
        List<String> spawnLore = Arrays.asList("§8§m-----------------------",
                ChatColor.GRAY + "[ Level: " + tycoonBlock.getSpawnRateLevel() + " ]",
                ChatColor.GRAY + "[ Spawnrate: " + tycoonBlock.getSpawnRateFormatted() + "s ]",
                ChatColor.GRAY + "[ Upgrade cost: " + ChatColor.GREEN + PriceUtility.formatMoney(TycoonUpgrades.getSpawnRateUpgradeCost(tycoonBlock,tycoonBlock.getSpawnRateLevel() + 1)) + ChatColor.GRAY +  " -> " + (tycoonBlock.getSpawnRateLevel() + 1) +" ]",
                "§8§m-----------------------");
        ItemStack spawnRate = MenuManager.createItemstack(Material.SPAWNER,
                1,
                ChatColor.DARK_PURPLE + "Upgrade Spawn rate",
                spawnLore,
                false,
                true,
                true,
                "upgradeSpawnRate");
        inventory.setItem(11,spawnRate);
        //</editor-fold>

        //<editor-fold desc="🎲Double Drops Upgrade">
        List<String> doubleDropsLore = Arrays.asList("§8§m-----------------------",
                ChatColor.GRAY + "[ Level: " + tycoonBlock.getTycoonUpgrades().getDoubleDropsLevel() + " ]",
                ChatColor.GRAY + "[ Chance: " + tycoonBlock.getDoubleDropsChanceFormatted() + " ]",
                ChatColor.GRAY + "[ Upgrade cost: " + ChatColor.GREEN + PriceUtility.formatMoney(TycoonUpgrades.getDoubleDropChanceUpgradeCost(tycoonBlock,tycoonBlock.getTycoonUpgrades().getDoubleDropsLevel() + 1)) + ChatColor.GRAY +  " -> " + (tycoonBlock.getTycoonUpgrades().getDoubleDropsLevel() + 1) +" ]",
                "§8§m-----------------------");
        ItemStack doubleDrops = MenuManager.createItemstack(
                Material.PRISMARINE_CRYSTALS,
                1,
                ChatColor.AQUA + "Upgrade Double Drops",
                doubleDropsLore,
                false,
                true,
                true,
                "upgradeDoubleDrops"
        );
        inventory.setItem(13,doubleDrops);
//</editor-fold>

        //<editor-fold desc="⛏️ Auto Miner Upgrade">
        if (tycoonBlock.getTycoonUpgrades().isAutoMinerUnlocked()){
            List<String> minerLore = Arrays.asList("§8§m-----------------------",
                    ChatColor.GRAY + "[ Level: " + tycoonBlock.getMiningRateLevel() + " ]",
                    ChatColor.GRAY + "[ Minerate: " + tycoonBlock.getMiningRateFormatted() + "s ]",
                    ChatColor.GRAY + "[ Upgrade Cost: " + ChatColor.GREEN + PriceUtility.formatMoney(TycoonUpgrades.getMiningRateUpgradeCost(tycoonBlock, tycoonBlock.getMiningRateLevel() + 1)) + ChatColor.GRAY +  " -> " + (tycoonBlock.getMiningRateLevel() + 1) +" ]",
                    "§8§m-----------------------");
            ItemStack autoMinerSpeed = MenuManager.createItemstack(Material.IRON_PICKAXE,
                    1,
                    ChatColor.GOLD + "Upgrade Automining speed",
                    minerLore,
                    false,
                    true,
                    true,
                    "upgradeAutoMinerSpeed");
            inventory.setItem(15,autoMinerSpeed);
        } else {
            ItemStack autoMinerLocked = MenuManager.createItemstack(Material.IRON_BARS,
                    1,
                    ChatColor.RED + "Locked",
                    null,
                    true,
                    true,
                    true,
                    "autoMinerLocked");
            inventory.setItem(15,autoMinerLocked);
        }
        //</editor-fold>

        //<editor-fold desc="🍀 Fortune Upgrade">
        List<String> fortuneLore = Arrays.asList("§8§m-----------------------",
                ChatColor.GRAY + "[ Level: " + tycoonBlock.getTycoonUpgrades().getFortuneLevel() + " ]",
                ChatColor.GRAY + "[ Chance: " + tycoonBlock.getFortuneChanceFormatted() + " ]",
                ChatColor.GRAY + "[ Upgrade cost: " + ChatColor.GREEN + PriceUtility.formatMoney(TycoonUpgrades.getFortuneUpgradeCost(tycoonBlock,tycoonBlock.getTycoonUpgrades().getFortuneLevel() + 1)) + ChatColor.GRAY +  " -> " + (tycoonBlock.getTycoonUpgrades().getFortuneLevel() + 1) +" ]",
                "§8§m-----------------------");
        ItemStack fortune = MenuManager.createItemstack(
                Material.BOOK,
                1,
                ChatColor.LIGHT_PURPLE + "Upgrade Fortune",
                fortuneLore,
                false,
                true,
                true,
                "upgradeFortune"
        );
        inventory.setItem(29,fortune);
//</editor-fold>

        //<editor-fold desc="💵 Multiplier Upgrade">
        List<String> multiplierLore = Arrays.asList("§8§m-----------------------",
                ChatColor.GRAY + "[ Level: " + tycoonBlock.getSellMultiplierLevel() + " ]",
                ChatColor.GRAY + "[ Sell Multiplier: " + tycoonBlock.getSellMultiplierFormatted() + "x ]",
                ChatColor.GRAY + "[ Upgrade Cost: " + ChatColor.GREEN + PriceUtility.formatMoney(TycoonUpgrades.getSellMultiplierUpgradeCost(tycoonBlock,tycoonBlock.getSellMultiplierLevel() + 1)) + ChatColor.GRAY +  " -> " + (tycoonBlock.getSellMultiplierLevel() + 1) +" ]",
                "§8§m-----------------------");
        ItemStack worthMultiplier = MenuManager.createItemstack(Material.LIME_BUNDLE,
                1,
                ChatColor.GREEN + "Upgrade Sell Multiplier",
                multiplierLore,
                false,
                true,
                true,
                "upgradeWorthMultiplier");
        inventory.setItem(31,worthMultiplier);
//</editor-fold>

        //<editor-fold desc="📦 Inventory Storage Upgrade">
        List<String> inventoryLore = Arrays.asList("§8§m-----------------------",
                ChatColor.GRAY + "[ Level: " + tycoonUpgrades.getInventoryStorageLevel()+ " ]",
                ChatColor.GRAY + "[ Storage: " + tycoonBlock.getInventoryStorage() + " blocks ]",
                ChatColor.GRAY + "[ Upgrade Cost: " + ChatColor.GREEN + PriceUtility.formatMoney(TycoonUpgrades.getInventoryStorageUpgradeCost(tycoonBlock,tycoonUpgrades.getInventoryStorageLevel() + 1)) + ChatColor.GRAY +  " -> " + (tycoonUpgrades.getInventoryStorageLevel() + 1) +" ]",
                "§8§m-----------------------");
        ItemStack inventoryUpgrade = MenuManager.createItemstack(Material.CHEST_MINECART,
                1,
                ChatColor.GOLD + "Upgrade Inventory Storage",
                inventoryLore,
                false,
                true,
                true,
                "upgradeInventoryStorage");
        inventory.setItem(33, inventoryUpgrade);
//</editor-fold>

        //<editor-fold desc="❌ Return button">
        ItemStack returnItem = MenuManager.createItemstack(Material.BARRIER,
                1,
                ChatColor.RED + "<- Back to Stats Menu",
                null,
                false,
                true,
                true,
                "return");
        inventory.setItem(44, returnItem);
        //</editor-fold>
    }

    @Override
    public void handleAction(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            String action = pdc.get(TycoonData.MENU_ACTION_KEY, PersistentDataType.STRING);

            switch (action) {
                case "upgradeSpawnRate":
                    tycoonBlock.upgradeSpawnRate(player);
                    refresh(player,inventory);
                    break;
                case "upgradeAutoMinerSpeed":
                    tycoonBlock.upgradeMiningRate(player);
                    refresh(player,inventory);
                    break;
                case "autoMinerLocked":
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_HIT, 1f, 1f);
                    break;
                case "upgradeWorthMultiplier":
                    tycoonBlock.upgradeSellMultiplier(player);
                    refresh(player,inventory);
                    break;
                case "upgradeDoubleDrops":
                    tycoonBlock.upgradeDoubleDropsChance(player);
                    refresh(player,inventory);
                    break;
                case "upgradeFortune":
                    tycoonBlock.upgradeFortuneChance(player);
                    refresh(player,inventory);
                    break;
                case "upgradeInventoryStorage":
                    tycoonBlock.upgradeMaxInventoryStorage(player);
                    tycoonBlock.updateHologramPreset(tycoonBlock.getLocation(), "STORAGE");
                    refresh(player,inventory);
                    break;
                case "return":
                    new StatsMenu(tycoonBlock, plugin).open(player);
                    break;
                case null:
                    break;
                default:
                    break;
            }
        }
    }
}
