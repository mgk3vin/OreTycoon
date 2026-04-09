package me.mangokevin.oreTycoon.tycoonManagment.spawnBlocks;

import org.bukkit.ChatColor;


public enum SpawnMaterialRarity {
    COMMON(ChatColor.GRAY + "Common", 30, 1),
    UNCOMMON(ChatColor.GREEN + "Uncommon", 50, 1.5),
    RARE(ChatColor.AQUA + "Rare", 70, 2),
    EPIC(ChatColor.LIGHT_PURPLE + "Epic", 100, 3),
    LEGENDARY(ChatColor.GOLD + "Legendary", 150, 5),;

    private final String displayName;
    private final int xpAmount;
    private final double worthMulti;

    SpawnMaterialRarity(String displayName, int xpAmount, double worthMulti) {
        this.displayName = displayName;
        this.xpAmount = xpAmount;
        this.worthMulti = worthMulti;
    }

    public String getDisplayName() {
        return displayName;
    }
    public int getXpAmount() {
        return xpAmount;
    }
    public double getWorthMulti() {
        return worthMulti;
    }
}
