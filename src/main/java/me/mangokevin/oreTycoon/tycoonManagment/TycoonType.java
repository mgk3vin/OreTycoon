package me.mangokevin.oreTycoon.tycoonManagment;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.Map;

public enum TycoonType {
    WOOD(Material.CRAFTING_TABLE, ChatColor.GOLD + "Wood Tycoon", 3, 5, Map.of(
            Material.ACACIA_LOG, 10,
            Material.BIRCH_LOG, 10,
            Material.CHERRY_LOG, 10,
            Material.JUNGLE_LOG, 10,
            Material.DARK_OAK_LOG, 10,
            Material.OAK_LOG, 10,
            Material.SPRUCE_LOG, 10
    )),
    COAL(Material.COAL_BLOCK, "§8Coal Tycoon", 5, 7, Map.of(
            Material.STONE, 60, Material.COAL_ORE, 30, Material.COBBLESTONE, 10
    )),
    IRON(Material.IRON_BLOCK, "§fIron Tycoon", 3, 5, Map.of(
            Material.STONE, 40, Material.IRON_ORE, 40, Material.RAW_IRON_BLOCK, 20
    )),
    DIAMOND(Material.DIAMOND_BLOCK, "§bDiamond Tycoon", 2, 4, Map.of(
            Material.DIAMOND_ORE, 50, Material.DEEPSLATE_DIAMOND_ORE, 40, Material.DIAMOND_BLOCK, 10
    ));

    private final Material material;
    private final String name;
    private final int spawnInterval;
    private final int miningInterval;
    private final Map<Material, Integer> resources;

    TycoonType(Material material, String name, int spawnInterval, int miningInterval, Map<Material, Integer> resources) {
        this.material = material;
        this.name = name;
        this.spawnInterval = spawnInterval;
        this.miningInterval = miningInterval;
        this.resources = resources;
    }



    public int getSpawnInterval() {
        return spawnInterval;
    }
    public Material getMaterial() {
        return material;
    }
    public String getName() {
        return name;
    }
    public int getMiningInterval() {
        return miningInterval;
    }
    public Map<Material, Integer> getResources() {
        return resources;
    }
}
