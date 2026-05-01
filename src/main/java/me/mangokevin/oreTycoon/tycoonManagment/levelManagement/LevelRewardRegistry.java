package me.mangokevin.oreTycoon.tycoonManagment.levelManagement;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import me.mangokevin.oreTycoon.tycoonManagment.booster.*;
import me.mangokevin.oreTycoon.utility.Console;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.checkerframework.common.reflection.qual.GetClass;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LevelRewardRegistry {
    public static final Map<Integer, LevelReward> LEVEL_REWARDS = new HashMap<>();
    public static int size;

    static {
        size = 500;
    }
    private static LevelReward createCashReward(int level, RewardTier rewardTier, Economy economy) {
        switch (rewardTier) {
            case COMMON -> {
                return new LevelReward(
                        Material.GOLD_INGOT,
                        ChatColor.GRAY + "Level: " + level,
                        Arrays.asList(
                                "§8§m-----------------------",
                                "§8Reward: " + ChatColor.GREEN + "+5k Cash",
                                "§8Tier: " + rewardTier.getDisplayName(),
                                "§8§m-----------------------"
                        ),
                        rewardTier,
                        player ->  {
                            economy.depositPlayer(player, 5000);
                        },
                        level
                );
            }
            case RARE -> {
                return new LevelReward(
                        Material.GOLD_INGOT,
                        ChatColor.GRAY + "Level: " + level,
                        Arrays.asList(
                                "§8§m-----------------------",
                                "§8Reward: " + ChatColor.GREEN + "+25k Cash",
                                "§8Tier: " + rewardTier.getDisplayName(),
                                "§8§m-----------------------"
                        ),
                        rewardTier,
                        player ->  {
                            economy.depositPlayer(player, 25000);
                        },
                        level
                );
            }
            case EPIC -> {
                return new LevelReward(
                        Material.GOLD_INGOT,
                        ChatColor.GRAY + "Level: " + level,
                        Arrays.asList(
                                "§8§m-----------------------",
                                "§8Reward: " + ChatColor.GREEN + "+100k Cash",
                                "§8Tier: " + rewardTier.getDisplayName(),
                                "§8§m-----------------------"
                        ),
                        rewardTier,
                        player ->  {
                            economy.depositPlayer(player, 100000);
                        },
                        level
                );
            }
            case LEGENDARY -> {
                return new LevelReward(
                        Material.GOLD_INGOT,
                        ChatColor.GRAY + "Level: " + level,
                        Arrays.asList(
                                "§8§m-----------------------",
                                "§8Reward: " + ChatColor.GREEN + "+500k Cash",
                                "§8Tier: " + rewardTier.getDisplayName(),
                                "§8§m-----------------------"
                        ),
                        rewardTier,
                        player ->  {
                            economy.depositPlayer(player, 500000);
                        },
                        level
                );
            }
            case null, default -> {
                Console.error(GetClass.class, "Invalid reward tier!");
                return null;
            }
        }
    }
    private static LevelReward createSellMultiplierReward(int level, RewardTier rewardTier, TycoonBlock tycoonBlock, Economy economy) {
        if (tycoonBlock.isSellMultiplierMaxed()) {
            return createCashReward(level, rewardTier, economy);
        } else {
            return new LevelReward(Material.EMERALD, ChatColor.GRAY + "Level: " + level,
                    Arrays.asList(
                            "§8§m-----------------------",
                            "§8Reward: " + ChatColor.GREEN + "+1 Sell Multiplier Level",
                            "§8Tier: " + rewardTier.getDisplayName(),
                            "§8§m-----------------------"
                    ),
                    rewardTier,
                    (player) -> {
                        tycoonBlock.upgradeSellMultiplier(player, true);
                    },
                    level
            );
        }
    }
    private static LevelReward createAutoMinerReward(int level, RewardTier rewardTier,TycoonBlock tycoonBlock, Economy economy) {
        if (tycoonBlock.isMiningRateMaxed()) {
            return createCashReward(level, rewardTier, economy);
        } else {
            return new LevelReward(Material.GOLDEN_PICKAXE, ChatColor.GRAY + "Level: " + level,
                    Arrays.asList(
                            "§8§m-----------------------",
                            "§8Reward: " + ChatColor.GOLD + "+1 Auto Miner Level",
                            "§8Tier: " + rewardTier.getDisplayName(),
                            "§8§m-----------------------"
                    ),
                    rewardTier,
                    (player) -> {
                        tycoonBlock.upgradeMiningRate(player, true);
                    },
                    level
            );
        }
    }
    private static LevelReward createMaxStorageReward(int level, RewardTier rewardTier,TycoonBlock tycoonBlock, Economy economy) {
        switch (rewardTier) {
            case COMMON -> {
                return new LevelReward(Material.CHEST, ChatColor.GRAY + "Level: " + level,
                    Arrays.asList(
                            "§8§m-----------------------",
                            "§8Reward: " + ChatColor.GOLD + "+3 Storage Level",
                            "§8Tier: " + rewardTier.getDisplayName(),
                            "§8§m-----------------------"
                    ),
                    rewardTier,
                    (player) -> {
                        for (int i = 0; i < 3; i++) {
                            tycoonBlock.upgradeMaxInventoryStorage(player, true);
                        }
                        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_LOCKED, 1.0f, 1.0f);
                    },
                        level
                );
            }
            case RARE -> {
                return new LevelReward(Material.CHEST, ChatColor.GRAY + "Level: " + level,
                        Arrays.asList(
                                "§8§m-----------------------",
                                "§8Reward: " + ChatColor.GOLD + "+5 Storage Level",
                                "§8Tier: " + rewardTier.getDisplayName(),
                                "§8§m-----------------------"
                        ),
                        rewardTier,
                        (player) -> {
                            for (int i = 0; i < 5; i++) {
                                tycoonBlock.upgradeMaxInventoryStorage(player, true);
                            }
                            player.playSound(player.getLocation(), Sound.BLOCK_CHEST_LOCKED, 1.0f, 1.0f);
                        },
                        level
                );
            }
            case EPIC -> {
                return new LevelReward(Material.CHEST, ChatColor.GRAY + "Level: " + level,
                        Arrays.asList(
                                "§8§m-----------------------",
                                "§8Reward: " + ChatColor.GOLD + "+10 Storage Level",
                                "§8Tier: " + rewardTier.getDisplayName(),
                                "§8§m-----------------------"
                        ),
                        rewardTier,
                        (player) -> {
                            for (int i = 0; i < 10; i++) {
                                tycoonBlock.upgradeMaxInventoryStorage(player, true);
                            }
                            player.playSound(player.getLocation(), Sound.BLOCK_CHEST_LOCKED, 1.0f, 1.0f);
                        },
                        level
                );
            }
            case LEGENDARY -> {
                return new LevelReward(Material.CHEST, ChatColor.GRAY + "Level: " + level,
                        Arrays.asList(
                                "§8§m-----------------------",
                                "§8Reward: " + ChatColor.GOLD + "+20 Storage Level",
                                "§8Tier: " + rewardTier.getDisplayName(),
                                "§8§m-----------------------"
                        ),
                        rewardTier,
                        (player) -> {
                            for (int i = 0; i < 20; i++) {
                                tycoonBlock.upgradeMaxInventoryStorage(player, true);
                            }
                            player.playSound(player.getLocation(), Sound.BLOCK_CHEST_LOCKED, 1.0f, 1.0f);
                        },
                        level
                );
            }
            case null, default -> {
                Console.error(GetClass.class, "Invalid reward tier!");
                return null;
            }
        }
    }
    private static LevelReward createSellMultiplierBoosterReward(int level, RewardTier rewardTier) {
        SellMultiplyBooster booster;
        switch (rewardTier) {
            case COMMON -> {
                booster = new SellMultiplyBooster(2, 20L * 60);
            }
            case RARE -> {
                booster = new SellMultiplyBooster(2.5, 20L * 120);
            }
            case EPIC -> {
                booster = new SellMultiplyBooster(3, 20L * 180);
            }
            case LEGENDARY -> {
                booster = new SellMultiplyBooster(4, 20L * 240);
            }
            case null, default -> {
                Console.error(GetClass.class, "Invalid reward tier!");
                return null;
            }
        }
        return new LevelReward(booster.getMaterial(), ChatColor.GRAY + "Level: " + level,
                Arrays.asList(
                        "§8§m-----------------------",
                        "§8Reward: " + booster.getDisplayName(),
                        "§8Boost: " + ChatColor.GREEN + "+ " + booster.getBoostValue() + "x sell multiplier",
                        "§8Duration: " + ChatColor.GREEN + booster.getRemainingTimeFormatted(booster.getDuration()),
                        "§8Tier: " + rewardTier.getDisplayName(),
                        "§8§m-----------------------"
                ),
                rewardTier,
                (player) -> {
                    player.getInventory().addItem(booster.getItem(1));
                },
                level
        );
    }
    private static LevelReward createAutoMinerBoosterReward(int level, RewardTier rewardTier) {
        AutoMinerSpeedBooster booster;
        switch (rewardTier) {
            case COMMON -> {
                booster = new AutoMinerSpeedBooster(20D * 0.3, 20L * 60);
            }
            case RARE -> {
                booster = new AutoMinerSpeedBooster(20D * 0.5, 20L * 120);
            }
            case EPIC -> {
                booster = new AutoMinerSpeedBooster(20D * 0.7, 20L * 180);
            }
            case LEGENDARY -> {
                booster = new AutoMinerSpeedBooster(20D * 1, 20L * 240);
            }
            case null, default -> {
                Console.error(GetClass.class, "Invalid reward tier!");
                return null;
            }
        }
        return new LevelReward(booster.getMaterial(), ChatColor.GRAY + "Level: " + level,
                Arrays.asList(
                        "§8§m-----------------------",
                        "§8Reward: " + booster.getDisplayName(),
                        "§8Boost: " + ChatColor.BLUE + "- " + booster.getBoostValue()/20 + "s AutoMiner speed",
                        "§8Duration: " + ChatColor.BLUE + booster.getRemainingTimeFormatted(booster.getDuration()),
                        "§8Tier: " + rewardTier.getDisplayName(),
                        "§8§m-----------------------"
                ),
                rewardTier,
                (player) -> {
                    player.getInventory().addItem(booster.getItem(1));
                },
                level
        );
    }
    private static LevelReward createSpawnSpeedBoosterReward(int level, RewardTier rewardTier) {
        SpawnSpeedBooster booster;
        switch (rewardTier) {
            case COMMON -> {
                booster = new SpawnSpeedBooster(20D * 0.3, 20L * 60);
            }
            case RARE -> {
                booster = new SpawnSpeedBooster(20D * 0.5, 20L * 120);
            }
            case EPIC -> {
                booster = new SpawnSpeedBooster(20D * 0.7, 20L * 180);
            }
            case LEGENDARY -> {
                booster = new SpawnSpeedBooster(20D * 1, 20L * 240);
            }
            case null, default -> {
                Console.error(GetClass.class, "Invalid reward tier!");
                return null;
            }
        }
        return new LevelReward(booster.getMaterial(), ChatColor.GRAY + "Level: " + level,
                Arrays.asList(
                        "§8§m-----------------------",
                        "§8Reward: " + booster.getDisplayName(),
                        "§8Boost: " + ChatColor.LIGHT_PURPLE + "-" + booster.getBoostValue()/20 + "s spawn speed",
                        "§8Duration: " + ChatColor.LIGHT_PURPLE + booster.getRemainingTimeFormatted(booster.getDuration()),
                        "§8Tier: " + rewardTier.getDisplayName(),
                        "§8§m-----------------------"
                ),
                rewardTier,
                (player) -> {
                    player.getInventory().addItem(booster.getItem(1));
                },
                level
        );
    }

    public static LevelReward getLevelReward(int level, TycoonBlock tycoonBlock) {
        Economy econ = OreTycoon.getEconomy();


        if (level <= 0 || level >= size) {
            return new LevelReward(Material.AIR, "", List.of(), RewardTier.COMMON, (player) -> {}, level);
        }
        //Special Level Rewards
        switch (level){
            case 5 -> {
                return new LevelReward(Material.IRON_PICKAXE, ChatColor.GRAY + "Level: " + level,
                        Arrays.asList(
                                "§8§m-----------------------",
                                "Reward: " + ChatColor.GOLD + "Unlock Auto Miner",
                                "Tier: " + RewardTier.RARE.getDisplayName(),
                                "§8§m-----------------------"
                        ),
                        RewardTier.RARE,
                        (player) -> {
                            tycoonBlock.getTycoonUpgrades().setAutoMinerUnlocked(true);
                            tycoonBlock.updateAttributes();
                            player.sendMessage(ChatColor.GREEN + "Unlocked Auto Miner for tycoon #" + tycoonBlock.getIndex() + "!");
                        },
                        level
                );
            }
            case 50 -> {
                return createSellMultiplierBoosterReward(level, RewardTier.RARE);
            }
            case 100 -> {
                return createSellMultiplierBoosterReward(level, RewardTier.EPIC);
            }
            case 200 -> {
                return createSellMultiplierBoosterReward(level, RewardTier.LEGENDARY);
            }
        }
        //EarlyGame 1-100
        if (level <= 100){
            //Every 10th Level
            if (level % 10 == 0) {
                //Switch between 2 rewards
                if (level % 20 == 0){
                    //Give Sell multi reward
                    return createSellMultiplierReward(level, RewardTier.COMMON, tycoonBlock, econ);
                } else {
                    return createAutoMinerReward(level, RewardTier.COMMON, tycoonBlock, econ);
                }
            }
            //Every 5th Level
            if (level % 5 == 0){
                if (level % 25 == 0){
                    return createAutoMinerBoosterReward(level, RewardTier.RARE);
                } else if (level % 35 == 0) {
                    return createSpawnSpeedBoosterReward(level, RewardTier.RARE);
                } else if (level % 15 == 0){
                    return createAutoMinerBoosterReward(level, RewardTier.COMMON);
                } else {
                    return createSpawnSpeedBoosterReward(level, RewardTier.COMMON);
                }
            }
            //Every 2nd Level
            if (level % 2 == 0){
                return createMaxStorageReward(level, RewardTier.COMMON, tycoonBlock, econ);
            } else {
                if (level >= 50){
                    return createCashReward(level, RewardTier.RARE, econ);
                } else {
                    return createCashReward(level, RewardTier.COMMON, econ);
                }
            }
        }
        if (level <= 200){
            //Every 10th Level
            if (level % 10 == 0) {
                //Switch between 2 rewards
                if (level % 20 == 0){
                    //Give Sell multi reward
                    return createSellMultiplierReward(level, RewardTier.RARE, tycoonBlock, econ);
                } else {
                    return createAutoMinerReward(level, RewardTier.RARE, tycoonBlock, econ);
                }
            }
            //Every 5th Level
            if (level % 5 == 0){
                if (level % 25 == 0){
                    return createAutoMinerBoosterReward(level, RewardTier.EPIC);
                } else if (level % 35 == 0) {
                    return createSpawnSpeedBoosterReward(level, RewardTier.EPIC);
                } else if (level % 15 == 0){
                    return createAutoMinerBoosterReward(level, RewardTier.RARE);
                } else {
                    return createSpawnSpeedBoosterReward(level, RewardTier.RARE);
                }
            }
            //Every 2nd Level
            if (level % 2 == 0){
                return createMaxStorageReward(level, RewardTier.RARE, tycoonBlock, econ);
            } else {
                if (level >= 150){
                    return createCashReward(level, RewardTier.EPIC, econ);
                } else {
                    return createCashReward(level, RewardTier.RARE, econ);
                }
            }
        }

        return new LevelReward(Material.BEDROCK, ChatColor.GRAY + "Default Level Reward",
                Arrays.asList(
                        "§8§m-----------------------",
                        "Reward: " + ChatColor.GOLD + "Unlock Auto Miner",
                        "Tier: " + RewardTier.LEGENDARY.getDisplayName(),
                        "§8§m-----------------------"
                ),
                RewardTier.LEGENDARY,
                (player) -> {
                    player.sendMessage(ChatColor.GRAY + "This is a default reward!");
                },
                level
        );
    }
    public static int getSize(){
        return size;
    }
}
