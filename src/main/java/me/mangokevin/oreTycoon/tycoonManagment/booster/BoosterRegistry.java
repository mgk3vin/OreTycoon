package me.mangokevin.oreTycoon.tycoonManagment.booster;

import me.mangokevin.oreTycoon.utility.Console;

public class BoosterRegistry {
    public static TycoonBoosterAbstract createBooster(String uid, double value, long duration){
        if (value == 0.0 || duration == 0L){
            Console.error("[BoosterRegistry] Invalid arguments for Booster");
            return null;
        }
        return switch (uid.toLowerCase()){
            case "sell_multiplier_booster" -> new SellMultiplyBooster(value, duration);
            case "auto_miner_booster" -> new AutoMinerSpeedBooster(value, duration);
            default -> null;
        };
    }
}
