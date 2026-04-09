package me.mangokevin.oreTycoon.tycoonManagment.levelManagement;

import org.bukkit.ChatColor;

public enum RewardTier {
    COMMON(ChatColor.GREEN + "Common"),
    RARE(ChatColor.AQUA + "Rare"),
    EPIC(ChatColor.LIGHT_PURPLE + "Epic"),
    LEGENDARY(ChatColor.GOLD + "Legendary"),;


    private final String displayName;

    RewardTier(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
