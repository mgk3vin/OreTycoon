package me.mangokevin.oreTycoon.tycoonManagment.booster;

import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import me.mangokevin.oreTycoon.utility.Console;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

public class SellMultiplyBooster extends TycoonBoosterAbstract {

    long duration;

    public SellMultiplyBooster(double boostValue, long duration) {
        super(boostValue, duration);
        this.duration = duration;
    }

    @Override
    public Material getMaterial() {
        return Material.AMETHYST_SHARD;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.GREEN + "" + ChatColor.ITALIC +  "Sell Multiplier Booster";
    }

    @Override
    public List<String> getLore() {
        return Arrays.asList("§8§m-----------------------",
                ChatColor.GREEN + "+ " + getBoostValue() + "x sell multiplier",
                ChatColor.GREEN + "Duration: " + getRemainingTimeFormatted(getDuration()),
                "§8§m-----------------------");
    }

    @Override
    public String getUID() {
        return "sell_multiplier_booster";
    }

    @Override
    public double getBoostValue() {
        return boostValue;
    }

    @Override
    public long getDuration() {
        return duration;
    }

    @Override
    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public void onApply(TycoonBlock tycoonBlock) {
        tycoonBlock.getTycoonBoosterManager().activate(this);
        Console.log("[SellMultiplierBooster] Activated Tycoon Booster");
    }


}
