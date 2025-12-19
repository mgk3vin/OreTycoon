package me.mangokevin.oreTycoon.levelManagment;

import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;

public class LevelManager {


     /* Quadratische Formel: 100 + (level^2 * 25)
     * Level 1 -> 125 XP
     * Level 2 -> 200 XP
     * Level 5 -> 725 XP
     * Level 10 -> 2600 XP
     */
    /**
     * Definiert, wie viel XP man INSGESAMT auf einem Level sammeln muss.
     * Wenn wir wollen, dass Level 1 -> Level 2 genau 200 XP braucht:
     */
    public int getXpNeededForLevel(int level) {
        int BASE_XP = 100;
        int MULTIPLIER = 25;
        System.out.println("[LevelManager] getXpNeededForLevel " + level + " is " + (BASE_XP + (level * level * MULTIPLIER)));
        return BASE_XP + (level * level * MULTIPLIER);
    }

    /**
     * Fortschrittsanzeige
     */
    public double getProgressPercentage(int xp, int level) {
        double needed = getXpNeededForLevel(level);
        if (needed <= 0) return 0.0;
        if (xp <= 0) return 0.0;

        // FIX: Cast auf (double) VOR der Division, sonst kommt 0 raus!
        System.out.println("[LevelManager] Xp needed is " + needed + " out of " + xp + " for level " + level + " | Percent: " + Math.round(((double) xp / needed) * 100.0));
        return Math.round(((double) xp / needed) * 100.0);
    }

    public boolean canLevelUp(int xp, int level) {
        System.out.println("[LevelManager] canLevelup is " + (xp >= getXpNeededForLevel(level + 1)) + " because " + xp + " | " + getXpNeededForLevel(level + 1));
        return xp >= getXpNeededForLevel(level + 1);
    }

    public void handleXpGain(TycoonBlock tycoonBlock, int gainedXp) {
        // WICHTIG: Nutze getLevelxp() (den aktuellen Fortschritt im Level)
        int currentXpProgress = tycoonBlock.getLevelXp();
        int currentLevel = tycoonBlock.getLevel();
        System.out.println("[LevelManager] Current xp: " + currentXpProgress + " Current Level: " + currentLevel);
        int totalXpInCurrentLevel = currentXpProgress + gainedXp;

        // Prüfen gegen die Kosten des aktuellen Levels
        while (canLevelUp(totalXpInCurrentLevel, currentLevel)) {
            totalXpInCurrentLevel -= getXpNeededForLevel(currentLevel + 1);
            currentLevel++;
            System.out.println("[LevelManager] Leveled up to " + currentLevel);
        }

        tycoonBlock.setLevelXp(totalXpInCurrentLevel);
        tycoonBlock.setLevel(currentLevel);
        System.out.println("[LevelManager] Set xp to: "  + totalXpInCurrentLevel + " Level to: " + currentLevel);
        tycoonBlock.updateHologramPreset(tycoonBlock.getLocation(), "XP");
        tycoonBlock.updateHologramPreset(tycoonBlock.getLocation(), "LEVEL");

    }

}
