package me.mangokevin.oreTycoon.levelManagment;

public class LevelManager {

    public LevelManager() {

    }

    public int getXpNeededForLevel(int level) {
        if (level <= 0) {
            return 0;
        }
        return (int) (100 * Math.pow(level, 1.4));
    }

    @Deprecated
    public int getLevelFromXp(int totalXp) {
        if (totalXp < 100) return 1;
        return (int) Math.pow(totalXp / 100.0, 1.0 / 1.4);
    }

    @Deprecated
    public double getProgressPercentage(int totalXp) {
        int currentLevel = getLevelFromXp(totalXp);
        int nextLevel = getLevelFromXp(currentLevel + 1);
        int currentLevelXp = getXpNeededForLevel(currentLevel);

        double progress = (double) (totalXp - currentLevelXp) / (nextLevel - currentLevelXp);
        return Math.min(100.0, progress * 100.0);
    }
}
