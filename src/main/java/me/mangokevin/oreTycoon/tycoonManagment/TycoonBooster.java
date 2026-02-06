package me.mangokevin.oreTycoon.tycoonManagment;

import me.mangokevin.oreTycoon.menuManager.MenuManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

@Deprecated
public enum TycoonBooster {


    SELL_BOOSTER(
            Material.AMETHYST_SHARD,
            ChatColor.GREEN + "" + ChatColor.ITALIC +"Sell Booster",
            Arrays.asList(
                    "§8§m-----------------------",
                    ChatColor.GOLD+ "Boosts the sell multiplier of your tycoon!",
                    ChatColor.GOLD + "Duration: " + ChatColor.ITALIC + " 7min",
                    "§8§m-----------------------"
            ),
            0.5,
            20L * 60 * 7
    ),
    AUTO_MINER_BOOSTER(
            Material.AMETHYST_SHARD,
            ChatColor.GOLD + "" + ChatColor.ITALIC + "Auto Miner Booster",
            Arrays.asList(
                    "§8§m-----------------------",
                    ChatColor.GOLD+ "Boosts the speed of your tycoon's autominer!",
                    ChatColor.GOLD + "Duration: " + ChatColor.ITALIC + " 5min",
                    "§8§m-----------------------"
            ),
            0.3,
            20L * 60 * 5
    );

    private final ItemStack item;
    private final List<String> lore;
    private final double boost;
    private final long duration;
    TycoonBooster(Material material, String name, List<String> lore, double boost, long duration){
        this.lore = lore;
        this.boost = boost;
        this.duration = duration;
        long formattedDuration = (duration / 60)/20;


        this.item = MenuManager.createItemstack(
                material,
                1,
                name,
                lore,
                true,
                true,
                true,
                "tycoon_booster_item"
        );
    }

    public ItemStack getItem() {
        return item.clone();
    }
    public double getBoost() {
        return boost;
    }
    public long getDuration() {
        return duration;
    }
}
