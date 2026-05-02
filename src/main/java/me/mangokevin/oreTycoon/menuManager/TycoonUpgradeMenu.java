package me.mangokevin.oreTycoon.menuManager;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.tycoonManagment.*;
import me.mangokevin.oreTycoon.tycoonManagment.upgrades.TycoonUpgrades;
import me.mangokevin.oreTycoon.tycoonManagment.upgrades.UpgradeResult;
import me.mangokevin.oreTycoon.worth.PriceUtility;
import net.milkbowl.vault.economy.Economy;
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
import java.util.ArrayList;
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

        UpgradeResult spawnRateUpgradeResult = canUpgradeXSpawnRateLevels(eco, player);

        List<String> spawnLore = new ArrayList<>(
                Arrays.asList("§8§m-----------------------",
                        ChatColor.GRAY + "[ Level: " + tycoonBlock.getSpawnRateLevel() + " ]",
                        ChatColor.GRAY + "[ Current spawn rate: " + tycoonBlock.getSpawnRateFormatted() + "s]",
                        ChatColor.GRAY + "[ Upgrade cost: " + (eco.has(player, spawnRateUpgradeCost) ? ChatColor.GREEN : ChatColor.RED) + spawnRateUpgradeString,
                        "§8§m-----------------------")
        );
        if (spawnRateUpgradeResult.cost() > 0) {
            spawnLore.add(ChatColor.YELLOW + "[ SHIFT LEFT CLICK ] " + ChatColor.GOLD + "To upgrade " + spawnRateUpgradeResult.level() +"x Levels");
            spawnLore.add(ChatColor.GRAY + "[ Cost: " + ChatColor.GREEN + PriceUtility.formatMoney(spawnRateUpgradeResult.cost()) + ChatColor.GRAY + " ]");
        }
        List<String> spawnRateMaxedLore = Arrays.asList(
                "§8§m-----------------------",
                ChatColor.GOLD + "[ Level: " + tycoonBlock.getSpawnRateLevel() +" MAXED ]",
                ChatColor.GOLD + "[ Current spawn rate: " + tycoonBlock.getSpawnRateFormatted() + "s]",
                "§8§m-----------------------");
        ItemStack spawnRate = MenuManager.createItemstack(Material.SPAWNER,
                1,
                ChatColor.DARK_PURPLE + "Upgrade Spawn rate",
                (tycoonBlock.isSpawnRateMaxed() ? spawnRateMaxedLore : spawnLore),
                tycoonBlock.isSpawnRateMaxed(),
                true,
                true,
                "upgradeSpawnRate");
        inventory.setItem(10 ,spawnRate);
        //</editor-fold>

        //<editor-fold desc="🎲Double Drops Upgrade">
        double doubleDropsUpgradeCost = TycoonUpgrades.getDoubleDropChanceUpgradeCost(tycoonBlock,tycoonBlock.getTycoonUpgrades().getDoubleDropsLevel() + 1);
        String doubleDropsUpgradeString = PriceUtility.formatMoney(doubleDropsUpgradeCost) +  ChatColor.GRAY +  " -> " + (tycoonBlock.getTycoonUpgrades().getDoubleDropsLevel() + 1) +" ]";

        UpgradeResult doubleDropsUR = canUpgradeXDoubleDropsLevels(eco, player);

        List<String> doubleDropsLore = new ArrayList<>(Arrays.asList("§8§m-----------------------",
                ChatColor.GRAY + "[ Level: " + tycoonBlock.getTycoonUpgrades().getDoubleDropsLevel() + " ]",
                ChatColor.GRAY + "[ Tier: " + tycoonBlock.getDoubleDropsTier() + " ]",
                ChatColor.GRAY + "[ Multiplier: " + tycoonBlock.getDoubleDropsAmount() + "x ]",
                ChatColor.GRAY + "[ Current Chance: " + tycoonBlock.getDoubleDropsChanceFormatted() + " ]",
                ChatColor.GRAY + "[ Upgrade cost: " + (eco.has(player, doubleDropsUpgradeCost) ? ChatColor.GREEN : ChatColor.RED) + doubleDropsUpgradeString ,
                "§8§m-----------------------"));
        if (doubleDropsUR.cost() > 0) {
            doubleDropsLore.add(ChatColor.YELLOW + "[ SHIFT LEFT CLICK ] " + ChatColor.GOLD + "To upgrade " + doubleDropsUR.level() + "x Levels");
            doubleDropsLore.add(ChatColor.GRAY + "[ Cost: " + ChatColor.GREEN + PriceUtility.formatMoney(doubleDropsUR.cost()) + ChatColor.GRAY + " ]");
        }

        List<String> doubleDropsMaxedLore = Arrays.asList("§8§m-----------------------",
                ChatColor.GOLD + "[ Level: " + tycoonBlock.getTycoonUpgrades().getDoubleDropsLevel() + " MAXED ]",
                ChatColor.GOLD + "[ Tier: " + tycoonBlock.getDoubleDropsTier() + " ]",
                ChatColor.GOLD + "[ Multiplier: " + tycoonBlock.getDoubleDropsAmount() + "x ]",
                "§8§m-----------------------");
        ItemStack doubleDrops = MenuManager.createItemstack(
                Material.PRISMARINE_CRYSTALS,
                1,
                ChatColor.AQUA + "Upgrade Double Drops",
                (tycoonBlock.isDoubleDropsChanceMaxed() ? doubleDropsMaxedLore : doubleDropsLore),
                tycoonBlock.isDoubleDropsChanceMaxed(),
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

            UpgradeResult minerUR = canUpgradeXAutoMinerLevels(eco, player);

            List<String> minerLore = new ArrayList<>(Arrays.asList("§8§m-----------------------",
                    ChatColor.GRAY + "[ Level: " + tycoonBlock.getMiningRateLevel() + " ]",
                    ChatColor.GRAY + "[ Current mine rate: " + tycoonBlock.getMiningRateFormatted() + "s ]",
                    ChatColor.GRAY + "[ Upgrade Cost: " + (eco.has(player, minerUpgradeCost) ? ChatColor.GREEN : ChatColor.RED) + minerUpgradeString,
                    "§8§m-----------------------"));
            if (minerUR.cost() > 0) {
                minerLore.add(ChatColor.YELLOW + "[ SHIFT LEFT CLICK ] " + ChatColor.GOLD + "To upgrade " + minerUR.level() + "x Levels");
                minerLore.add(ChatColor.GRAY + "[ Cost: " + ChatColor.GREEN + PriceUtility.formatMoney(minerUR.cost()) + ChatColor.GRAY + " ]");
            }

            List<String> minerMaxedLore = Arrays.asList(
                    "§8§m-----------------------",
                    ChatColor.GOLD + "[ Level: " + tycoonBlock.getMiningRateLevel() + " MAXED ]",
                    ChatColor.GOLD + "[ Current mine rate: " + tycoonBlock.getMiningRateFormatted() + "s ]",
                    "§8§m-----------------------"
            );
            ItemStack autoMinerSpeed = MenuManager.createItemstack(Material.IRON_PICKAXE,
                    1,
                    ChatColor.GOLD + "Upgrade Automining speed",
                    (tycoonBlock.isMiningRateMaxed() ? minerMaxedLore : minerLore),
                    tycoonBlock.isMiningRateMaxed(),
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

        UpgradeResult  multiMinerUR = canUpgradeXMultiMinerLevels(eco, player);

        List<String> multiMinerLore = new ArrayList<>(Arrays.asList("§8§m-----------------------",
                ChatColor.GRAY + "[ Level: " + tycoonBlock.getTycoonUpgrades().getMultipleMinerLevel() + " ]",
                ChatColor.GRAY + "[ Tier: " + tycoonBlock.getMultiMinerTier() + " ]",
                ChatColor.GRAY + "[ Multiplier: " + tycoonBlock.getMultiMinerAmount() + "x ]",
                ChatColor.GRAY + "[ Current Chance: " + tycoonBlock.getMultiMinerChanceFormatted() + " ]",
                ChatColor.GRAY + "[ Upgrade cost: " + (eco.has(player, multiMinerUpgradeCost) ? ChatColor.GREEN : ChatColor.RED) + multiMinerUpgradeString ,
                "§8§m-----------------------"));
        if (multiMinerUR.cost() > 0) {
            multiMinerLore.add(ChatColor.YELLOW + "[ SHIFT LEFT CLICK ] " + ChatColor.GOLD + "To upgrade " + multiMinerUR.level() + "x Levels");
            multiMinerLore.add(ChatColor.GRAY + "[ Cost: " + ChatColor.GREEN + PriceUtility.formatMoney(multiMinerUR.cost()) + ChatColor.GRAY + " ]");
        }
        List<String> multiMinerMaxedLore = Arrays.asList("§8§m-----------------------",
                ChatColor.GOLD + "[ Level: " + tycoonBlock.getTycoonUpgrades().getMultipleMinerLevel() + " MAXED ]",
                ChatColor.GOLD + "[ Tier: " + tycoonBlock.getMultiMinerTier() + " ]",
                ChatColor.GOLD + "[ Multiplier: " + tycoonBlock.getMultiMinerAmount() + "x ]",
                "§8§m-----------------------");
        ItemStack multiMiner = MenuManager.createItemstack(
                Material.TNT,
                1,
                ChatColor.AQUA + "Upgrade Multi Miner",
                (tycoonBlock.isMultiMinerChanceMaxed() ? multiMinerMaxedLore : multiMinerLore),
                tycoonBlock.isMultiMinerChanceMaxed(),
                true,
                true,
                "upgradeMultiMiner"
        );
        inventory.setItem(16, multiMiner);
        //</editor-fold>

        //<editor-fold desc="🍀 Fortune Upgrade">
        double fortuneUpgradeCost = TycoonUpgrades.getFortuneUpgradeCost(tycoonBlock,tycoonBlock.getTycoonUpgrades().getFortuneLevel() + 1);
        String fortuneUpgradeString = PriceUtility.formatMoney(fortuneUpgradeCost) + ChatColor.GRAY + " -> " + (tycoonBlock.getTycoonUpgrades().getFortuneLevel() + 1) +" ]";

        UpgradeResult fortuneUR = canUpgradeXFortuneLevels(eco, player);

        List<String> fortuneLore = new ArrayList<>(Arrays.asList("§8§m-----------------------",
                ChatColor.GRAY + "[ Level: " + tycoonBlock.getTycoonUpgrades().getFortuneLevel() + " ]",
                ChatColor.GRAY + "[ Tier: " + tycoonBlock.getFortuneTier() + " ]",
                ChatColor.GRAY + "[ Multiplier: " + tycoonBlock.getFortuneMultiplier() + "x ]",
                ChatColor.GRAY + "[ Current Chance: " + tycoonBlock.getFortuneChanceFormatted() + " ]",
                ChatColor.GRAY + "[ Upgrade cost: " + (eco.has(player, fortuneUpgradeCost) ? ChatColor.GREEN : ChatColor.RED) + fortuneUpgradeString ,
                "§8§m-----------------------"));
        if (fortuneUR.cost() > 0) {
            fortuneLore.add(ChatColor.YELLOW + "[ SHIFT LEFT CLICK ] " + ChatColor.GOLD + "To upgrade " + fortuneUR.level() + "x Levels");
            fortuneLore.add(ChatColor.GRAY + "[ Cost: " + ChatColor.GREEN + PriceUtility.formatMoney(fortuneUR.cost()) + ChatColor.GRAY + " ]");
        }
        List<String> fortuneMaxedLore = Arrays.asList("§8§m-----------------------",
                ChatColor.GOLD + "[ Level: " + tycoonBlock.getTycoonUpgrades().getFortuneLevel() + " MAXED ]",
                ChatColor.GOLD + "[ Tier: " + tycoonBlock.getFortuneTier() + " ]",
                ChatColor.GOLD + "[ Multiplier: " + tycoonBlock.getFortuneMultiplier() + "x ]",
                "§8§m-----------------------");
        ItemStack fortune = MenuManager.createItemstack(
                Material.BOOK,
                1,
                ChatColor.LIGHT_PURPLE + "Upgrade Fortune",
                (tycoonBlock.isFortuneChanceMaxed() ? fortuneMaxedLore : fortuneLore),
                tycoonBlock.isFortuneChanceMaxed(),
                true,
                true,
                "upgradeFortune"
        );
        inventory.setItem(29,fortune);
//</editor-fold>

        //<editor-fold desc="💵 Multiplier Upgrade">
        double sellMultiplierUpgradeCost = TycoonUpgrades.getSellMultiplierUpgradeCost(tycoonBlock,tycoonBlock.getSellMultiplierLevel() + 1);
        String sellMultiplierUpgradeString =  PriceUtility.formatMoney(sellMultiplierUpgradeCost) + ChatColor.GRAY +  " -> " + (tycoonBlock.getSellMultiplierLevel() + 1) +" ]";

        UpgradeResult sellMultiplierUR = canUpgradeXSellMultiplierLevels(eco, player);

        List<String> multiplierLore = new ArrayList<>(Arrays.asList("§8§m-----------------------",
                ChatColor.GRAY + "[ Level: " + tycoonBlock.getSellMultiplierLevel() + " ]",
                ChatColor.GRAY + "[ Current sell multiplier: " + tycoonBlock.getSellMultiplierFormatted() + "x ]",
                ChatColor.GRAY + "[ Upgrade Cost: " + (eco.has(player, sellMultiplierUpgradeCost) ? ChatColor.GREEN : ChatColor.RED) + sellMultiplierUpgradeString,
                "§8§m-----------------------"));
        if (sellMultiplierUR.cost() > 0) {
            multiplierLore.add(ChatColor.YELLOW + "[ SHIFT LEFT CLICK ] " + ChatColor.GOLD + "To upgrade " + sellMultiplierUR.level() + "x Levels");
            multiplierLore.add(ChatColor.GRAY + "[ Cost: " + ChatColor.GREEN + PriceUtility.formatMoney(sellMultiplierUR.cost()) + ChatColor.GRAY + " ]");
        }
        List<String> multiplierMaxedLore = Arrays.asList("§8§m-----------------------",
                ChatColor.GOLD + "[ Level: " + tycoonBlock.getSellMultiplierLevel() + " MAXED ]",
                ChatColor.GOLD + "[ Current sell multiplier: " + tycoonBlock.getSellMultiplierFormatted() + "x ]",
                "§8§m-----------------------");
        ItemStack worthMultiplier = MenuManager.createItemstack(Material.LIME_BUNDLE,
                1,
                ChatColor.GREEN + "Upgrade Sell Multiplier",
                (tycoonBlock.isSellMultiplierMaxed() ? multiplierMaxedLore : multiplierLore),
                tycoonBlock.isSellMultiplierMaxed(),
                true,
                true,
                "upgradeWorthMultiplier");
        inventory.setItem(31,worthMultiplier);
//</editor-fold>

        //<editor-fold desc="📦 Inventory Storage Upgrade">
        double storageUpgradeCost = TycoonUpgrades.getInventoryStorageUpgradeCost(tycoonBlock,tycoonUpgrades.getInventoryStorageLevel() + 1);
        String storageUpgradeCostString = PriceUtility.formatMoney(storageUpgradeCost) + ChatColor.GRAY +  " -> " + (tycoonUpgrades.getInventoryStorageLevel() + 1) +" ]";

        UpgradeResult storageUR = canUpgradeXInventoryStorageLevels(eco, player);

        List<String> inventoryLore = new ArrayList<>(Arrays.asList("§8§m-----------------------",
                ChatColor.GRAY + "[ Level: " + tycoonUpgrades.getInventoryStorageLevel()+ " ]",
                ChatColor.GRAY + "[ Current storage: " + tycoonBlock.getInventoryStorage() + " blocks ]",
                ChatColor.GRAY + "[ Upgrade Cost: " + (eco.has(player, storageUpgradeCost) ? ChatColor.GREEN : ChatColor.RED) + storageUpgradeCostString ,
                "§8§m-----------------------"));
        if (storageUR.cost() > 0) {
            inventoryLore.add(ChatColor.YELLOW + "[ SHIFT LEFT CLICK ] " + ChatColor.GOLD + "To upgrade " + storageUR.level() + "x Levels");
            inventoryLore.add(ChatColor.GRAY + "[ Cost: " + ChatColor.GREEN + PriceUtility.formatMoney(storageUR.cost()) + ChatColor.GRAY + " ]");
        }
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

    private UpgradeResult canUpgradeXSpawnRateLevels(Economy econ, Player player) {
        double cost = 0;
        for (int i = 0; i < 10; i++) {
            int level = tycoonBlock.getTycoonUpgrades().getSpawnRateLevel() + i + 1;
            long spawnRate = TycoonUpgrades.calculateNewSpawnRate(level, tycoonBlock.getTycoonType().getSpawnInterval());
            double costDif = TycoonUpgrades.getSpawnRateUpgradeCost(tycoonBlock, level);
            if (!econ.has(player, cost + costDif) || spawnRate < tycoonBlock.getMin_spawn_rate()) {
                return new UpgradeResult(i, cost);
            }
            cost += costDif;
        }
        return new UpgradeResult(10, cost);
    }
    private UpgradeResult canUpgradeXAutoMinerLevels(Economy econ, Player player) {
        double cost = 0;
        for (int i = 0; i < 10; i++) {
            int level = tycoonBlock.getTycoonUpgrades().getMiningRateLevel() + i + 1;
            long miningRate = TycoonUpgrades.calculateNewMiningRate(level, tycoonBlock.getTycoonType().getMiningInterval());
            double costDif = TycoonUpgrades.getMiningRateUpgradeCost(tycoonBlock, level);
            if (!econ.has(player, cost + costDif) || miningRate < tycoonBlock.getMin_mining_rate()) {
                return new UpgradeResult(i, cost);
            }
            cost += costDif;
        }
        return new UpgradeResult(10, cost);
    }
    private UpgradeResult canUpgradeXDoubleDropsLevels(Economy econ, Player player) {
        double cost = 0;
        for (int i = 0; i < 10; i++) {
            int level = tycoonBlock.getTycoonUpgrades().getDoubleDropsLevel() + i + 1;
            double rawChance = TycoonUpgrades.calculateNewDoubleDropChance(level, 0);
            double costDif = TycoonUpgrades.getDoubleDropChanceUpgradeCost(tycoonBlock, level);
            if (!econ.has(player, cost + costDif) || rawChance > tycoonBlock.getMaxRawDoubleDropsChance()) {
                return new UpgradeResult(i, cost);
            }
            cost += costDif;
        }
        return new UpgradeResult(10, cost);
    }
    private UpgradeResult canUpgradeXFortuneLevels(Economy econ, Player player) {
        double cost = 0;
        for (int i = 0; i < 10; i++) {
            int level = tycoonBlock.getTycoonUpgrades().getFortuneLevel() + i + 1;
            double rawChance = TycoonUpgrades.calculateNewFortuneChance(level, 0);
            double costDif = TycoonUpgrades.getFortuneUpgradeCost(tycoonBlock, level);
            if (!econ.has(player, cost + costDif) || rawChance > tycoonBlock.getMaxRawFortuneChance()) {
                return new UpgradeResult(i, cost);
            }
            cost += costDif;
        }
        return new UpgradeResult(10, cost);
    }
    private UpgradeResult canUpgradeXMultiMinerLevels(Economy econ, Player player) {
        double cost = 0;
        for (int i = 0; i < 10; i++) {
            int level = tycoonBlock.getTycoonUpgrades().getMultipleMinerLevel() + i + 1;
            double rawChance = TycoonUpgrades.calculateMultipleMinerChance(level, 0);
            double costDif = TycoonUpgrades.getMultipleMinerUpgradeCost(tycoonBlock, level);
            if (!econ.has(player, cost + costDif) || rawChance > tycoonBlock.getMaxRawMultiMinerChance()) {
                return new UpgradeResult(i, cost);
            }
            cost += costDif;
        }
        return new UpgradeResult(10, cost);
    }
    private UpgradeResult canUpgradeXSellMultiplierLevels(Economy econ, Player player) {
        double cost = 0;
        for (int i = 0; i < 10; i++) {
            int level = tycoonBlock.getTycoonUpgrades().getSellMultiplierLevel() + i + 1;
            double sellMultiplier = TycoonUpgrades.calculateNewSellMultiplier(level, tycoonBlock.getTycoonType().getSellMultiplier());
            double costDif = TycoonUpgrades.getSellMultiplierUpgradeCost(tycoonBlock, level);
            if (!econ.has(player, cost + costDif) || sellMultiplier > tycoonBlock.getMaxSellMultiplier()) {
                return new UpgradeResult(i, cost);
            }
            cost += costDif;
        }
        return new UpgradeResult(10, cost);
    }
    private UpgradeResult canUpgradeXInventoryStorageLevels(Economy econ, Player player) {
        double cost = 0;
        for (int i = 0; i < 10; i++) {
            int level = tycoonBlock.getTycoonUpgrades().getInventoryStorageLevel() + i + 1;
            int maxInventoryStorage = TycoonUpgrades.getMaxInventoryStorage(level, tycoonBlock.getTycoonType().getDefaultMaxInventoryStorage());
            double costDif = TycoonUpgrades.getInventoryStorageUpgradeCost(tycoonBlock, level);
            if (!econ.has(player, cost + costDif)) {
                return new UpgradeResult(i, cost);
            }
            cost += costDif;
        }
        return new UpgradeResult(10, cost);
    }



    @Override
    public void handleAction(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        ItemMeta meta = item.getItemMeta();
        ClickType clickType = event.getClick();

        if (meta != null) {
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            String action = pdc.get(TycoonData.MENU_ACTION_KEY, PersistentDataType.STRING);

            switch (action) {
                case "upgradeSpawnRate":
                    if (clickType == ClickType.SHIFT_LEFT) {
                        tycoonBlock.upgradeSpawnRate(player, false, 10);
                    } else {
                        tycoonBlock.upgradeSpawnRate(player, false);
                    }
                    break;
                case "upgradeAutoMinerSpeed":
                    if (clickType == ClickType.SHIFT_LEFT) {
                        tycoonBlock.upgradeMiningRate(player, false, 10);
                    } else {
                        tycoonBlock.upgradeMiningRate(player, false);
                    }
                    break;
                case "autoMinerLocked":
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_HIT, 1f, 1f);
                    break;
                case "upgradeMultiMiner":
                    if (clickType == ClickType.SHIFT_LEFT) {
                        tycoonBlock.upgradeMultiMinerChance(player, false, 10);
                    } else {
                        tycoonBlock.upgradeMultiMinerChance(player, false);
                    }
                    break;
                case "upgradeWorthMultiplier":
                    if (clickType == ClickType.SHIFT_LEFT) {
                        tycoonBlock.upgradeSellMultiplier(player, false, 10);
                    } else {
                        tycoonBlock.upgradeSellMultiplier(player, false);
                    }
                    break;
                case "upgradeDoubleDrops":
                    if (clickType == ClickType.SHIFT_LEFT) {
                        tycoonBlock.upgradeDoubleDropsChance(player, false, 10);
                    } else {
                        tycoonBlock.upgradeDoubleDropsChance(player, false);
                    }
                    break;
                case "upgradeFortune":
                    if (clickType == ClickType.SHIFT_LEFT) {
                        tycoonBlock.upgradeFortuneChance(player, false, 10);
                    } else {
                        tycoonBlock.upgradeFortuneChance(player, false);
                    }
                    break;
                case "upgradeInventoryStorage":
                    if (clickType == ClickType.SHIFT_LEFT) {
                        tycoonBlock.upgradeMaxInventoryStorage(player, false, 10);
                    } else {
                        tycoonBlock.upgradeMaxInventoryStorage(player, false);
                    }
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
