package me.mangokevin.oreTycoon.levelManagment;

import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import org.bukkit.entity.Player;

public class LevelManager {

    public LevelManager() {

    }

    public int getXpForLevel(int level) {
        if (level <= 1) {
            return 0;
        }
        return (int) (100 * Math.pow(level, 2));
    }

    public int getLevelFromXp(int totalXp) {
        if (totalXp < 100) return 1;
        return (int) Math.sqrt(totalXp / 100.0);
    }


    public double getProgressPercentage(int totalXp) {
        int currentLevel = getLevelFromXp(totalXp);
        int nextLevel = getLevelFromXp(currentLevel + 1);
        int currentLevelXp = getXpForLevel(currentLevel);

        double progress = (double) (totalXp - currentLevelXp) / (nextLevel - currentLevelXp);
        return Math.min(100.0, progress * 100.0);
    }
}
