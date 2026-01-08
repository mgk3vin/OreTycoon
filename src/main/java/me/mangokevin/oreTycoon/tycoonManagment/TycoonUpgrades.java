package me.mangokevin.oreTycoon.tycoonManagment;

public class TycoonUpgrades {
    private int spawnRateLevel = 1;
    private int miningRateLevel = 1;
    private int sellMultiplierLevel = 1;


    public TycoonUpgrades(){};


    public static int calculateNewSpawnRate(int level, int defaultSpawnRate){
        return defaultSpawnRate - (level * 2);
    }
    public static int calculateNewMiningRate(int level, int defaultMiningRate){
        return defaultMiningRate - (level * 2);
    }
    public static double calculateNewSellMultiplier(int level, double defaultSellMultiplier){
        return (Math.round((defaultSellMultiplier + (level * 0.01)) * 100.0)/100.0);
    }

    public static double getSpawnRateUpgradeCost(int level){
        double base = 100.0;
        double multi = 1.4;
        return Math.round(getUpgradeCost(level, base, multi));
    }
    public static double getMiningRateUpgradeCost(int level){
        double base = 200.0;
        double multi = 1.2;
        return Math.round(getUpgradeCost(level, base, multi));
    }
    public static double getSellMultiplierUpgradeCost(int level){
        double base = 1000.0;
        double multi = 1.2;
        return Math.round(getUpgradeCost(level, base, multi));
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
    //==========  Setter  ==========
    //==========  Getter  ==========
    public int getSpawnRateLevel() {
        return spawnRateLevel;
    }
    public int getMiningRateLevel() {
        return miningRateLevel;
    }
    public int getSellMultiplierLevel() {
        return sellMultiplierLevel;
    }
    //==========  Getter  ==========
}
