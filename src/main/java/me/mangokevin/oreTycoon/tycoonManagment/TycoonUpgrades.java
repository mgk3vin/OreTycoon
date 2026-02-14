package me.mangokevin.oreTycoon.tycoonManagment;

import me.mangokevin.oreTycoon.utility.Console;
import me.mangokevin.oreTycoon.worth.PriceUtility;

import java.util.ArrayList;
import java.util.List;

public class TycoonUpgrades {
    private int spawnRateLevel = 0;
    private int miningRateLevel = 0;
    private int sellMultiplierLevel = 0;
    private int inventoryStorageLevel = 0;
    private int doubleDropsLevel = 0;
    private int fortuneLevel = 0;

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
        return (Math.round((defaultSellMultiplier + (level * 0.1)) * 100.0)/100.0);
    }
    public static double calculateNewDoubleDropChance(int level, double defaultDoubleDropChance){
        return defaultDoubleDropChance +  (level * 1.0);
    }
    public static double calculateNewFortuneChance(int level, double defaultFortuneChance){
        return defaultFortuneChance +  (level * 1.0);
    }
    public static int getMaxInventoryStorage(int level, int defaultMaxStorage){
        return  defaultMaxStorage + (5 * level);
    }
    public static int calculateMaxInventoryStorage(int level, int defaultMaxStorage){
        return  defaultMaxStorage + (5 * level);
    }

    public static double getSpawnRateUpgradeCost(TycoonBlock tycoonBlock, int level){
        double base = tycoonBlock.getTycoonType().getBasePrice();
        double multi = 1.17;
        return Math.round(getExponentialUpgradeCost(level, base, multi));
    }
    public static double getMiningRateUpgradeCost(TycoonBlock tycoonBlock, int level){
        double base = tycoonBlock.getTycoonType().getBasePrice();
        double multi = 1.15;
        return Math.round(getExponentialUpgradeCost(level, base, multi));
    }
    public static double getSellMultiplierUpgradeCost(TycoonBlock tycoonBlock, int level){
        double base = tycoonBlock.getTycoonType().getBasePrice() * 10.0;
        double multi = 1.3;
        return Math.round(getExponentialUpgradeCost(level, base, multi));
    }
    public static double getDoubleDropChanceUpgradeCost(TycoonBlock tycoonBlock, int level){
        double base = tycoonBlock.getTycoonType().getBasePrice();
        double multi = 1.15;
        return Math.round(getExponentialUpgradeCost(level, base, multi));
    }
    public static double getFortuneUpgradeCost(TycoonBlock tycoonBlock, int level){
        double base = tycoonBlock.getTycoonType().getBasePrice();
        double multi = 1.13;
        return Math.round(getExponentialUpgradeCost(level, base, multi));
    }
    public static double getInventoryStorageUpgradeCost(TycoonBlock tycoonBlock, int level){
        double base = tycoonBlock.getTycoonType().getBasePrice() * 10.0;
        double multi = 1.05;
        return Math.round(getExponentialUpgradeCost(level, base, multi));
    }

    public static void testUpgradeCostFunction( int level,double base, double multi){
        Console.debug("[TycoonUpgrades] Testing upgrade cost function...");
        for(int i = 0; i <= level; i++){
            Console.debug("[TycoonUpgrades] Level: " + i + " | Cost: " + PriceUtility.formatMoney(Math.round(getExponentialUpgradeCost(i, base, multi))));
        }
        Console.debug("[TycoonUpgrades] Testing done.");


    }



    public static double getExponentialUpgradeCost(int level, double basePrice, double multiplier){
        return basePrice * Math.pow(multiplier, level);
    }
    public static double getQuadraticUpgradeCost(int level, double basePrice, double multiplier){
        return basePrice * (level * level *  multiplier);
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
    public void setDoubleDropsLevel(int doubleDropsLevel){
        this.doubleDropsLevel = doubleDropsLevel;
    }
    public void setBuffed(boolean isBuffed){
        this.isBuffed = isBuffed;
    }
    public void setAutoMinerUnlocked(boolean autoMinerUnlocked){
        this.isAutoMinerUnlocked = autoMinerUnlocked;
    }
    public void setFortuneLevel(int fortuneLevel){
        this.fortuneLevel = fortuneLevel;
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
    public int getDoubleDropsLevel() {
        return doubleDropsLevel;
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
    public int getFortuneLevel() {
        return fortuneLevel;
    }
    //==========  Getter  ==========
}
