package me.mangokevin.oreTycoon.menuManager;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.tycoonManagment.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
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
        MenuManager.addFiller(inventory, Material.GRAY_STAINED_GLASS_PANE);
        //  20 | 22 | 24
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
        inventory.setItem(20,spawnRate);

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
            inventory.setItem(22,autoMinerSpeed);
        } else {
            ItemStack autoMinerLocked = MenuManager.createItemstack(Material.IRON_BARS,
                    1,
                    ChatColor.RED + "Locked",
                    null,
                    true,
                    true,
                    true,
                    "autoMinerLocked");
            inventory.setItem(22,autoMinerLocked);
        }



        List<String> multiplierLore = Arrays.asList("§8§m-----------------------",
                ChatColor.GRAY + "[ Level: " + tycoonBlock.getSellMultiplierLevel() + " ]",
                ChatColor.GRAY + "[ Sell Multiplier: " + tycoonBlock.getSellMultiplierFormatted() + "x ]",
                ChatColor.GRAY + "[ Upgrade Cost: " + ChatColor.GREEN + PriceUtility.formatMoney(TycoonUpgrades.getSellMultiplierUpgradeCost(tycoonBlock,tycoonBlock.getSellMultiplierLevel() + 1)) + ChatColor.GRAY +  " -> " + (tycoonBlock.getSellMultiplierLevel() + 1) +" ]",
                "§8§m-----------------------");
        ItemStack worthMultiplier = MenuManager.createItemstack(Material.LIME_BUNDLE,
                1,
                ChatColor.GREEN + "Upgrade Money Multiplier",
                multiplierLore,
                false,
                true,
                true,
                "upgradeWorthMultiplier");
        inventory.setItem(24,worthMultiplier);

        //Return button
        ItemStack returnItem = MenuManager.createItemstack(Material.BARRIER,
                1,
                ChatColor.RED + "<- Back to Stats Menu",
                null,
                false,
                true,
                true,
                "return");
        inventory.setItem(44, returnItem);
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
