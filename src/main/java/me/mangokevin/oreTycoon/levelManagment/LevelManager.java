package me.mangokevin.oreTycoon.levelManagment;

import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import me.mangokevin.oreTycoon.utility.Console;
import org.bukkit.Effect;
import org.bukkit.Sound;

import java.util.Objects;

public class LevelManager {



    public int getXpNeededForLevel(int level) {
        int BASE_XP = 50;
        double MULTIPLIER = 0.43;
        return (int)(BASE_XP + (level * level * MULTIPLIER));
    }

    public double getProgressPercentage(int xp, int level) {
        double needed = getXpNeededForLevel(level);
        if (needed <= 0) return 0.0;
        if (xp <= 0) return 0.0;

        return Math.round(((double) xp / needed) * 100.0);
    }

    public boolean canLevelUp(int xp, int level) {
        return xp >= getXpNeededForLevel(level + 1);
    }

    public void handleXpGain(TycoonBlock tycoonBlock, int gainedXp) {
        int currentXpProgress = tycoonBlock.getLevelXp();
        int currentLevel = tycoonBlock.getLevel();
        int totalXpInCurrentLevel = currentXpProgress + gainedXp;

        while (canLevelUp(totalXpInCurrentLevel, currentLevel)) {
            totalXpInCurrentLevel -= getXpNeededForLevel(currentLevel + 1);
            currentLevel++;
            Objects.requireNonNull(tycoonBlock.getLocation().getWorld()).playEffect(tycoonBlock.getLocation(), Effect.MOBSPAWNER_FLAMES, 1);
            tycoonBlock.getLocation().getWorld().playSound(tycoonBlock.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1.5f);
            tycoonBlock.callTycoonUpdateEvent();
        }

        tycoonBlock.setLevelXp(totalXpInCurrentLevel);
        tycoonBlock.setLevel(currentLevel);
        tycoonBlock.updateHologram();
    }

}
