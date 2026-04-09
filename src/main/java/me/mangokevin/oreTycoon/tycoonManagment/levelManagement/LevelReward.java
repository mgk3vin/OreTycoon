package me.mangokevin.oreTycoon.tycoonManagment.levelManagement;

import me.mangokevin.oreTycoon.menuManager.MenuManager;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class LevelReward {
    private final Material displayMaterial;
    private final String displayName;
    private final List<String> description;
    private final RewardAction rewardAction;
    private final RewardTier rewardTier;
    private final int level;

    public LevelReward(Material material, String displayName, List<String> description,RewardTier rewardTier, RewardAction rewardAction, int level) {
        this.displayMaterial = material;
        this.displayName = displayName;
        this.description = description;
        this.rewardTier = rewardTier;
        this.rewardAction = rewardAction;
        this.level = level;
    }

    public void apply(Player player) {
        rewardAction.apply(player);
    }

    public ItemStack getDisplayItem() {
        ItemStack displayItem =  MenuManager.createItemstack(
                displayMaterial,
                1,
                displayName,
                description,
                true,
                true,
                true,
                "level_reward_item"
        );
        MenuManager.addNameSpacedKey(TycoonData.LEVEL_REWARD_ID_KEY, displayItem, level);
        return displayItem;
    }
    public String getDisplayName() { return displayName; }
    public List<String> getDescription() { return description; }
    public Material getDisplayMaterial() {
        return displayMaterial;
    }
    public RewardAction getRewardAction() {
        return rewardAction;
    }
    public RewardTier getRewardTier() {
        return rewardTier;
    }
}
