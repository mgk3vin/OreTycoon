package me.mangokevin.oreTycoon.tycoonManagment.booster;

import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import me.mangokevin.oreTycoon.utility.Console;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.List;

public class SpawnSpeedBooster extends TycoonBoosterAbstract {
    long duration;
    public SpawnSpeedBooster(double boostValue, long duration) {
        super(boostValue, duration);
        this.duration = duration;
    }

    @Override
    public Material getMaterial() {
        return Material.PRISMARINE_SHARD;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.AQUA +  "" + ChatColor.ITALIC +  "Spawn Speed Booster";
    }

    @Override
    public List<String> getLore() {
        return List.of("§8§m-----------------------",
                ChatColor.GREEN + "-" + getBoostValue()/20 + "s spawn speed",
                ChatColor.GREEN + "Duration: " + getRemainingTimeFormatted(getDuration()),
                "§8§m-----------------------");
    }

    @Override
    public String getUID() {
        return "spawn_speed_booster";
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
        Console.log(getClass(), " Activated Tycoon Booster");
    }
}
