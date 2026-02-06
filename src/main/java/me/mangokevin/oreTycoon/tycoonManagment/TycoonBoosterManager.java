package me.mangokevin.oreTycoon.tycoonManagment;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.menuManager.MenuManager;
import me.mangokevin.oreTycoon.tycoonEvents.TycoonBoosterTickedEvent;
import me.mangokevin.oreTycoon.tycoonManagment.booster.AutoMinerSpeedBooster;
import me.mangokevin.oreTycoon.tycoonManagment.booster.SellMultiplyBooster;
import me.mangokevin.oreTycoon.tycoonManagment.booster.SpawnSpeedBooster;
import me.mangokevin.oreTycoon.tycoonManagment.booster.TycoonBoosterAbstract;
import me.mangokevin.oreTycoon.utility.Console;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.mvplugins.multiverse.external.vavr.collection.List;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TycoonBoosterManager {

    private boolean isAutoMinerBoosterActive = false;
    private long autoMinerBoostTime = 0L;

    private boolean isSpawnSpeedBoosterActive = false;

    private boolean isSellMultiplierBoosterActive = false;
    private double sellMultiplierBoost = 0.5;
    private long sellMultiplierBoostTime = 0L;

    // Aktive Booster-Werte
    private double currentSellBoost = 0.0;
    //private double currentSpeedBoost = 0.0;

    private final Map<String, Long> expirationTimes = new HashMap<>();

    private final OreTycoon plugin;
    private final TycoonBlock tycoonBlock;
    public TycoonBoosterManager(OreTycoon plugin, TycoonBlock tycoonBlock) {
        this.plugin = plugin;
        this.tycoonBlock = tycoonBlock;
    }

    @Deprecated
    public ItemStack createAutoMinerBooster(int amount) {
        return MenuManager.createItemstack(Material.AMETHYST_SHARD,
                amount,
                ChatColor.DARK_PURPLE + "" + ChatColor.ITALIC + "Auto Miner Booster",
                null,
                true,
                true,
                true,
                "tycoon_booster_item");
    }
    @Deprecated
    public ItemStack createSellMultiplierBooster(int amount) {
        return MenuManager.createItemstack(Material.AMETHYST_SHARD,
                amount,
                ChatColor.GREEN + "" + ChatColor.ITALIC + "Sell Multiplier Booster",
                Arrays.asList(ChatColor.GREEN + "Adds 0.5x Sell Multiplier"),
                true,
                true,
                true,
                "tycoon_booster_item");
    }

    public void activate(TycoonBoosterAbstract tycoonBooster) {

        long expiryTime = System.currentTimeMillis() + (tycoonBooster.getDuration() * 50);

        expirationTimes.put(tycoonBooster.getUID(), expiryTime);

        switch (tycoonBooster.getUID().toLowerCase()) {
            case "sell_multiplier_booster":
                isSellMultiplierBoosterActive = true;
                tycoonBlock.setSellMultiplierBooster((SellMultiplyBooster) tycoonBooster);
                runBoostTask(tycoonBooster, tycoonBooster.getDuration(), () -> {
                    this.isSellMultiplierBoosterActive = false;
                    tycoonBlock.setSellMultiplierBooster(null);
                    tycoonBlock.updateAttributes();
                });
                Console.log("[TycoonBoosterManager] SellMultiplierBoost activated");
                break;
            case "auto_miner_booster":
                isAutoMinerBoosterActive = true;
                tycoonBlock.setAutoMinerSpeedBooster((AutoMinerSpeedBooster) tycoonBooster);
                runBoostTask(tycoonBooster, tycoonBooster.getDuration(), () -> {
                    this.isAutoMinerBoosterActive = false;
                    tycoonBlock.setAutoMinerSpeedBooster(null);
                    tycoonBlock.updateAttributes();
                    Bukkit.getPluginManager().callEvent(new TycoonBoosterTickedEvent(tycoonBlock, tycoonBooster));
                });
                Console.log("[TycoonBoosterManager] AutoMinerSpeedBoost activated");
            case "spawn_speed_booster":
                isSpawnSpeedBoosterActive = true;
                tycoonBlock.setSpawnSpeedBooster((SpawnSpeedBooster) tycoonBooster);
                runBoostTask(tycoonBooster, tycoonBooster.getDuration(), () -> {
                    this.isSpawnSpeedBoosterActive = false;
                    tycoonBlock.setSpawnSpeedBooster(null);
                    tycoonBlock.updateAttributes();
                    Bukkit.getPluginManager().callEvent(new TycoonBoosterTickedEvent(tycoonBlock, tycoonBooster));
                });
                Console.log("[TycoonBoosterManager] AutoMinerSpeedBoost activated");
                break;
            default:
                break;
        }
        tycoonBlock.updateAttributes();

    }
    @Deprecated
    private void runSellMultiplierBoostTask(TycoonBoosterAbstract tycoonBooster) {
        new BukkitRunnable() {
            long tickCounter = 0L;
            @Override
            public void run() {
                if (tycoonBlock == null || !plugin.isEnabled()) {
                    Console.error("[TycoonBoosterManager] TycoonBlock is null or plugin is null");
                    this.cancel();
                    return;
                }

                tickCounter += 20L;
                tycoonBooster.setDuration(sellMultiplierBoostTime - tickCounter);
                Console.log("[TycoonBoosterManager] Time Remaining: " + (sellMultiplierBoostTime - tickCounter) + " ticks");
                Bukkit.getPluginManager().callEvent(new TycoonBoosterTickedEvent(tycoonBlock, tycoonBooster));
                if (tickCounter >= sellMultiplierBoostTime) {
                    Console.log("[TycoonBoosterManager] Cancelling SellMultiplierBoostTask after " + tickCounter + " ticks.");
                    isSellMultiplierBoosterActive = false;
                    this.cancel();
                }
                Console.log("[TycoonBoosterManager] SellMultiplierBoostTask ticking");
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }
    private void runBoostTask(TycoonBoosterAbstract tycoonBooster, long boostTime, Runnable onFinish) {
        new BukkitRunnable() {
            long tickCounter = 0L;
            @Override
            public void run() {
                if (tycoonBlock == null || !plugin.isEnabled()) {
                    Console.error("[TycoonBoosterManager] TycoonBlock is null or plugin is null");
                    this.cancel();
                    return;
                }

                tickCounter += 20L;
                tycoonBooster.setDuration(boostTime - tickCounter);
                //Console.log(getClass(), "Booster Duration: " + tycoonBooster.getDuration() + " ticks");
                //Console.log("[TycoonBoosterManager] Time Remaining: " + (boostTime - tickCounter) + " ticks");
                Bukkit.getPluginManager().callEvent(new TycoonBoosterTickedEvent(tycoonBlock, tycoonBooster));
                if (tickCounter >= boostTime) {
                    //Console.log("[TycoonBoosterManager] Cancelling BoostTask after " + tickCounter + " ticks.");
                    this.cancel();
                    if (onFinish != null) {
                        onFinish.run();
                    }
                    return;
                }
                //Console.log("[TycoonBoosterManager] SellMultiplierBoostTask ticking");
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public void setSellMultiplierBoostTime(long sellMultiplierBoostTime) {
        this.sellMultiplierBoostTime = sellMultiplierBoostTime;
    }
    public void setSellMultiplierBoosterActive(boolean isSellMultiplierBoosterActive) {
        this.isSellMultiplierBoosterActive = isSellMultiplierBoosterActive;
    }
    public void setAutoMinerBoosterActive(boolean isAutoMinerBoosterActive) {
        this.isAutoMinerBoosterActive = isAutoMinerBoosterActive;
    }
    public boolean isAutoMinerBoosterActive() {
        return isAutoMinerBoosterActive;
    }
    public boolean isSellMultiplierBoosterActive() {
        return isSellMultiplierBoosterActive;
    }
    public boolean isSpawnSpeedBoosterActive(){
        return isSpawnSpeedBoosterActive;
    }
    public double getSellMultiplierBoost() {
        if (isSellMultiplierBoosterActive) {
            return sellMultiplierBoost;
        }else {
            return 0.0;
        }
    }
}
