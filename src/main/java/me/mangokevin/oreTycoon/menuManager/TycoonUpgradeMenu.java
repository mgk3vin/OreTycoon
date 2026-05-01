package me.mangokevin.oreTycoon.menuManager;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.tycoonManagment.*;
import me.mangokevin.oreTycoon.worth.PriceUtility;
import net.milkbowl.vault.economy.Economy;
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
        Economy eco = OreTycoon.getEconomy();
        TycoonUpgrades tycoonUpgrades = tycoonBlock.getTycoonUpgrades();
        MenuManager.addFiller(inventory, Material.ORANGE_STAINED_GLASS_PANE);

        //  10 | 12 | 14
        //<editor-fold desc="⚒️ Spawn rate Upgrade">
        double spawnRateUpgradeCost = TycoonUpgrades.getSpawnRateUpgradeCost(tycoonBlock,tycoonBlock.getSpawnRateLevel() + 1);
        String spawnRateUpgradeString = PriceUtility.formatMoney(spawnRateUpgradeCost) + ChatColor.GRAY +  " -> " + (tycoonBlock.getSpawnRateLevel() + 1) +" ]";
        List<String> spawnLore = Arrays.asList("§8§m-----------------------",
                ChatColor.GRAY + "[ Level: " + tycoonBlock.getSpawnRateLevel() + " ]",
                ChatColor.GRAY + "[ Current spawn rate: " + tycoonBlock.getSpawnRateFormatted() + "s]",
                ChatColor.GRAY + "[ Upgrade cost: " + (eco.has(player, spawnRateUpgradeCost) ? ChatColor.GREEN : ChatColor.RED) + spawnRateUpgradeString,
                "§8§m-----------------------");

        ItemStack spawnRate = MenuManager.createItemstack(Material.SPAWNER,
                1,
                ChatColor.DARK_PURPLE + "Upgrade Spawn rate",
                spawnLore,
                false,
                true,
                true,
                "upgradeSpawnRate");
        inventory.setItem(10 ,spawnRate);
        //</editor-fold>

        //<editor-fold desc="🎲Double Drops Upgrade">
        double doubleDropsUpgradeCost = TycoonUpgrades.getDoubleDropChanceUpgradeCost(tycoonBlock,tycoonBlock.getTycoonUpgrades().getDoubleDropsLevel() + 1);
        String doubleDropsUpgradeString = PriceUtility.formatMoney(doubleDropsUpgradeCost) +  ChatColor.GRAY +  " -> " + (tycoonBlock.getTycoonUpgrades().getDoubleDropsLevel() + 1) +" ]";
        List<String> doubleDropsLore = Arrays.asList("§8§m-----------------------",
                ChatColor.GRAY + "[ Level: " + tycoonBlock.getTycoonUpgrades().getDoubleDropsLevel() + " ]",
                ChatColor.GRAY + "[ Tier: " + tycoonBlock.getDoubleDropsTier() + " ]",
                ChatColor.GRAY + "[ Multiplier: " + tycoonBlock.getDoubleDropsAmount() + "x ]",
                ChatColor.GRAY + "[ Current Chance: " + tycoonBlock.getDoubleDropsChanceFormatted() + " ]",
                ChatColor.GRAY + "[ Upgrade cost: " + (eco.has(player, doubleDropsUpgradeCost) ? ChatColor.GREEN : ChatColor.RED) + doubleDropsUpgradeString ,
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
        inventory.setItem(12,doubleDrops);
//</editor-fold>

        //<editor-fold desc="⛏️ Auto Miner Upgrade">
        if (tycoonBlock.getTycoonUpgrades().isAutoMinerUnlocked()){
            double minerUpgradeCost = TycoonUpgrades.getMiningRateUpgradeCost(tycoonBlock, tycoonBlock.getMiningRateLevel() + 1);
            String minerUpgradeString = PriceUtility.formatMoney(minerUpgradeCost) + ChatColor.GRAY +  " -> " + (tycoonBlock.getMiningRateLevel() + 1) +" ]";
            List<String> minerLore = Arrays.asList("§8§m-----------------------",
                    ChatColor.GRAY + "[ Level: " + tycoonBlock.getMiningRateLevel() + " ]",
                    ChatColor.GRAY + "[ Current mine rate: " + tycoonBlock.getMiningRateFormatted() + "s ]",
                    ChatColor.GRAY + "[ Upgrade Cost: " + (eco.has(player, minerUpgradeCost) ? ChatColor.GREEN : ChatColor.RED) + minerUpgradeString,
                    "§8§m-----------------------");
            ItemStack autoMinerSpeed = MenuManager.createItemstack(Material.IRON_PICKAXE,
                    1,
                    ChatColor.GOLD + "Upgrade Automining speed",
                    minerLore,
                    false,
                    true,
                    true,
                    "upgradeAutoMinerSpeed");
            inventory.setItem(14,autoMinerSpeed);
        } else {
            ItemStack autoMinerLocked = MenuManager.createItemstack(Material.IRON_BARS,
                    1,
                    ChatColor.RED + "Locked",
                    null,
                    true,
                    true,
                    true,
                    "autoMinerLocked");
            inventory.setItem(14,autoMinerLocked);
        }
        //</editor-fold>

        //<editor-fold desc="🔨 Multiple Miner Upgrade">
        double multiMinerUpgradeCost = TycoonUpgrades.getMultipleMinerUpgradeCost(tycoonBlock,tycoonBlock.getTycoonUpgrades().getMultipleMinerLevel() + 1);
        String multiMinerUpgradeString = PriceUtility.formatMoney(multiMinerUpgradeCost) +  ChatColor.GRAY +  " -> " + (tycoonBlock.getTycoonUpgrades().getMultipleMinerLevel() + 1) +" ]";
        List<String> multiMinerLore = Arrays.asList("§8§m-----------------------",
                ChatColor.GRAY + "[ Level: " + tycoonBlock.getTycoonUpgrades().getMultipleMinerLevel() + " ]",
                ChatColor.GRAY + "[ Tier: " + tycoonBlock.getMultiMinerTier() + " ]",
                ChatColor.GRAY + "[ Multiplier: " + tycoonBlock.getMultiMinerAmount() + "x ]",
                ChatColor.GRAY + "[ Current Chance: " + tycoonBlock.getMultiMinerChanceFormatted() + " ]",
                ChatColor.GRAY + "[ Upgrade cost: " + (eco.has(player, multiMinerUpgradeCost) ? ChatColor.GREEN : ChatColor.RED) + multiMinerUpgradeString ,
                "§8§m-----------------------");
        ItemStack multiMiner = MenuManager.createItemstack(
                Material.TNT,
                1,
                ChatColor.AQUA + "Upgrade Multi Miner",
                multiMinerLore,
                false,
                true,
                true,
                "upgradeMultiMiner"
        );
        inventory.setItem(16, multiMiner);
        //</editor-fold>

        //<editor-fold desc="🍀 Fortune Upgrade">
        double fortuneUpgradeCost = TycoonUpgrades.getFortuneUpgradeCost(tycoonBlock,tycoonBlock.getTycoonUpgrades().getFortuneLevel() + 1);
        String fortuneUpgradeString = PriceUtility.formatMoney(fortuneUpgradeCost) + ChatColor.GRAY + " -> " + (tycoonBlock.getTycoonUpgrades().getFortuneLevel() + 1) +" ]";
        List<String> fortuneLore = Arrays.asList("§8§m-----------------------",
                ChatColor.GRAY + "[ Level: " + tycoonBlock.getTycoonUpgrades().getFortuneLevel() + " ]",
                ChatColor.GRAY + "[ Tier: " + tycoonBlock.getFortuneTier() + " ]",
                ChatColor.GRAY + "[ Multiplier: " + tycoonBlock.getFortuneMultiplier() + "x ]",
                ChatColor.GRAY + "[ Current Chance: " + tycoonBlock.getFortuneChanceFormatted() + " ]",
                ChatColor.GRAY + "[ Upgrade cost: " + (eco.has(player, fortuneUpgradeCost) ? ChatColor.GREEN : ChatColor.RED) + fortuneUpgradeString ,
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
        double sellMultiplierUpgradeCost = TycoonUpgrades.getSellMultiplierUpgradeCost(tycoonBlock,tycoonBlock.getSellMultiplierLevel() + 1);
        String sellMultiplierUpgradeString =  PriceUtility.formatMoney(sellMultiplierUpgradeCost) + ChatColor.GRAY +  " -> " + (tycoonBlock.getSellMultiplierLevel() + 1) +" ]";
        List<String> multiplierLore = Arrays.asList("§8§m-----------------------",
                ChatColor.GRAY + "[ Level: " + tycoonBlock.getSellMultiplierLevel() + " ]",
                ChatColor.GRAY + "[ Current sell multiplier: " + tycoonBlock.getSellMultiplierFormatted() + "x ]",
                ChatColor.GRAY + "[ Upgrade Cost: " + (eco.has(player, sellMultiplierUpgradeCost) ? ChatColor.GREEN : ChatColor.RED) + sellMultiplierUpgradeString,
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
        double storageUpgradeCost = TycoonUpgrades.getInventoryStorageUpgradeCost(tycoonBlock,tycoonUpgrades.getInventoryStorageLevel()) + 1;
        String storageUpgradeCostString = PriceUtility.formatMoney(storageUpgradeCost) + ChatColor.GRAY +  " -> " + (tycoonUpgrades.getInventoryStorageLevel() + 1) +" ]";
        List<String> inventoryLore = Arrays.asList("§8§m-----------------------",
                ChatColor.GRAY + "[ Level: " + tycoonUpgrades.getInventoryStorageLevel()+ " ]",
                ChatColor.GRAY + "[ Current storage: " + tycoonBlock.getInventoryStorage() + " blocks ]",
                ChatColor.GRAY + "[ Upgrade Cost: " + (eco.has(player, storageUpgradeCost) ? ChatColor.GREEN : ChatColor.RED) + storageUpgradeCostString ,
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
                    break;
                case "upgradeAutoMinerSpeed":
                    tycoonBlock.upgradeMiningRate(player);
                    break;
                case "autoMinerLocked":
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_HIT, 1f, 1f);
                    break;
                case "upgradeMultiMiner":
                    tycoonBlock.upgradeMultiMinerChance(player, false);
                    break;
                case "upgradeWorthMultiplier":
                    tycoonBlock.upgradeSellMultiplier(player);
                    break;
                case "upgradeDoubleDrops":
                    tycoonBlock.upgradeDoubleDropsChance(player);
                    break;
                case "upgradeFortune":
                    tycoonBlock.upgradeFortuneChance(player);
                    break;
                case "upgradeInventoryStorage":
                    tycoonBlock.upgradeMaxInventoryStorage(player);
                    tycoonBlock.updateHologram();
                    break;
                case "return":
                    new StatsMenu(tycoonBlock, plugin).open(player);
                    break;
                case null, default:
                    break;
            }
            refresh(player,inventory);
        }
    }
}
