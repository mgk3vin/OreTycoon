package me.mangokevin.oreTycoon.tycoonManagment;

import me.mangokevin.oreTycoon.menuManager.MenuManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class TycoonBooster {

    private boolean isAutoMinerBoosterActive = false;
    private boolean isSellMultiplierBoosterActive = false;
    private double sellMultiplierBoost = 0.5;


    public ItemStack createAutoMinerBooster(int amount) {
        return MenuManager.createItemstack(Material.AMETHYST_SHARD,
                amount,
                ChatColor.DARK_PURPLE + "" + ChatColor.ITALIC + "Auto Miner Booster",
                null,
                true,
                true,
                "tycoon_booster_item");
    }
    public ItemStack createSellMultiplierBooster(int amount) {
        return MenuManager.createItemstack(Material.AMETHYST_SHARD,
                amount,
                ChatColor.GREEN + "" + ChatColor.ITALIC + "Sell Multiplier Booster",
                Arrays.asList(ChatColor.GREEN + "Adds 0.5x Sell Multiplier"),
                true,
                true,
                "tycoon_booster_item");
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
    public double getSellMultiplierBoost() {
        if (isSellMultiplierBoosterActive) {
            return sellMultiplierBoost;
        }else {
            return 0.0;
        }
    }
}
