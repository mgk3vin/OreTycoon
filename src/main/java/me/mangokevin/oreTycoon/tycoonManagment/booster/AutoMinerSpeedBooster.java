package me.mangokevin.oreTycoon.tycoonManagment.booster;

import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

public class AutoMinerSpeedBooster extends TycoonBoosterAbstract {
    private long duration;
    public AutoMinerSpeedBooster(double boostValue, long duration) {
        super(boostValue, duration);
        this.duration = duration;

    }

    @Override
    public Material getMaterial() {
        return Material.ECHO_SHARD;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.GOLD + "" + ChatColor.ITALIC + "Auto Miner Booster";
    }

    @Override
    public List<String> getLore() {
        return Arrays.asList(
                "§8§m-----------------------",
                ChatColor.GREEN + "- " + getBoostValue()/20 + "s AutoMiner speed",
                ChatColor.GREEN + "Duration: " + getRemainingTimeFormatted(getDuration()),
                "§8§m-----------------------"
        );
    }

    @Override
    public String getUID() {
        return "auto_miner_booster";
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
    }
}
