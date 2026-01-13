package me.mangokevin.oreTycoon.tycoonManagment;

import java.util.ArrayList;
import java.util.List;

public class TycoonUpgrades {
    private int spawnRateLevel = 1;
    private int miningRateLevel = 1;
    private int sellMultiplierLevel = 1;
    private int inventoryStorageLevel = 1;

    private boolean isBuffed = false;
    private double sellMultiplierBuff = 1.0;

    private boolean isAutoMinerUnlocked = false;
    public TycoonUpgrades(){};

    private List<Integer> claimedLevels = new ArrayList<>();

    public boolean hasClaimedLevel(int level){
        return claimedLevels.contains(level);
    }
    public void claimLevel(int level){
        claimedLevels.add(level);
    }
    public static int calculateNewSpawnRate(int level, int defaultSpawnRate){
        return defaultSpawnRate - (level * 2);
    }
    public static int calculateNewMiningRate(int level, int defaultMiningRate){
        return defaultMiningRate - (level * 2);
    }
    public static double calculateNewSellMultiplier(int level, double defaultSellMultiplier){
        return (Math.round((defaultSellMultiplier + (level * 0.01)) * 100.0)/100.0);
    }
    public boolean shouldAutoMinerUnlocked(int level){
        if (level >= 5){
            return true;
        }else {
            return false;
        }
    }
    public static double getSpawnRateUpgradeCost(TycoonBlock tycoonBlock, int level){
        double base = tycoonBlock.getTycoonType().getBasePrice();
        double multi = 1.4;
        return Math.round(getUpgradeCost(level, base, multi));
    }
    public static double getMiningRateUpgradeCost(TycoonBlock tycoonBlock, int level){
        double base = tycoonBlock.getTycoonType().getBasePrice();
        double multi = 1.2;
        return Math.round(getUpgradeCost(level, base, multi));
    }
    public static double getSellMultiplierUpgradeCost(TycoonBlock tycoonBlock, int level){
        double base = tycoonBlock.getTycoonType().getBasePrice() * 10.0;
        double multi = 1.2;
        return Math.round(getUpgradeCost(level, base, multi));
    }
    public static int getMaxInventoryStorage(int level, int defaultMaxStorage){
        return  defaultMaxStorage + (5 * level);
    }



    public static double getUpgradeCost(int level, double basePrice, double multiplier){
        return basePrice * Math.pow(multiplier, level - 1);
    }

    //==========  Setter  ==========
    public void setSpawnRateLevel(int spawnRateLevel){
        this.spawnRateLevel = spawnRateLevel;
    }
    public void setMiningRateLevel(int miningRateLevel){
        this.miningRateLevel = miningRateLevel;
    }
    public void setSellMultiplierLevel(int sellMultiplierLevel){
        this.sellMultiplierLevel = sellMultiplierLevel;
    }
    public void setInventoryStorageLevel(int inventoryStorageLevel){
        this.inventoryStorageLevel = inventoryStorageLevel;
    }
    public void setClaimedLevels(List<Integer> claimedLevels){
        this.claimedLevels = claimedLevels;
    }
    public void setSellMultiplierBuff(double sellMultiplierBuff){
        this.sellMultiplierBuff = sellMultiplierBuff;
    }
    public void setBuffed(boolean isBuffed){
        this.isBuffed = isBuffed;
    }
    public void setAutoMinerUnlocked(boolean autoMinerUnlocked){
        this.isAutoMinerUnlocked = autoMinerUnlocked;
    }
    //==========  Setter  ==========
    //==========  Getter  ==========
    public List<Integer> getClaimedLevels(){
        return claimedLevels;
    }
    public int getInventoryStorageLevel(){
        return inventoryStorageLevel;
    }
    public int getSpawnRateLevel() {
        return spawnRateLevel;
    }
    public int getMiningRateLevel() {
        return miningRateLevel;
    }
    public int getSellMultiplierLevel() {
        return sellMultiplierLevel;
    }
    public double getSellMultiplierBuff() {
        return sellMultiplierBuff;
    }
    public boolean isBuffed() {
        return isBuffed;
    }
    public boolean isAutoMinerUnlocked() {
        return isAutoMinerUnlocked;
    }
    //==========  Getter  ==========
}
