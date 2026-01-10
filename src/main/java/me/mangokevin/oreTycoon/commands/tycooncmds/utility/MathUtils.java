package me.mangokevin.oreTycoon.commands.tycooncmds.utility;

@Deprecated
public class MathUtils {


    public static double calculateWorthPerHour(double speed, double averageReward){
        return (3600/speed) * averageReward;
    }
}
