package me.mangokevin.oreTycoon.tycoonManagment;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum TycoonType {
    WOOD(
            Material.CRAFTING_TABLE,
            ChatColor.GOLD + "Wood Tycoon",
            10.0,
            5*20,
            7*20,
            1.0,
            30,
            Map.of(
                Material.ACACIA_LOG, 10,
                Material.BIRCH_LOG, 10,
                Material.CHERRY_LOG, 10,
                Material.JUNGLE_LOG, 10,
                Material.DARK_OAK_LOG, 10,
                Material.OAK_LOG, 10,
                Material.SPRUCE_LOG, 10
    ), Arrays.asList(Material.OAK_LEAVES,
                    Material.JUNGLE_LEAVES)),
    NETHER(
            Material.NETHERRACK,
            ChatColor.RED + "Nether Tycoon",
            50.0,
            20 * 5,
            20 * 5,
            1.0,
            50,
            new HashMap<Material, Integer>() {{
                put(Material.NETHERRACK, 30);
                put(Material.NETHER_BRICKS, 20);
                put(Material.NETHER_QUARTZ_ORE, 10);
                put(Material.SOUL_SAND, 30);
                put(Material.SOUL_SOIL, 30);
                put(Material.BASALT, 20);
                put(Material.BLACKSTONE, 15);
                put(Material.GLOWSTONE, 15);
                put(Material.MAGMA_BLOCK, 20);
                put(Material.NETHER_GOLD_ORE, 10);
                put(Material.ANCIENT_DEBRIS, 5);
                put(Material.GILDED_BLACKSTONE, 15);
            }},
            List.of(
                    Material.AIR
            )
    ),
    STONE(Material.STONE,
            ChatColor.GRAY + "Stone Tycoon",
            20.0,
            5*20,
            6*20,
            1.0,
            40,
            Map.of(
                    Material.STONE, 15,
                    Material.COBBLESTONE, 15,
                    Material.ANDESITE, 10,
                    Material.DIORITE, 10,
                    Material.GRANITE, 10,
                    Material.COBBLED_DEEPSLATE, 5,
                    Material.TUFF, 15,
                    Material.DIRT, 20,
                    Material.CALCITE, 7,
                    Material.SMOOTH_BASALT, 5
                    ),
            Arrays.asList(
                    Material.FURNACE
            )),

    COAL(Material.COAL_BLOCK,
            "§8Coal Tycoon",
            30.0,
            5*20,
            7*20,
            1.0,
            40,
            Map.of(
                Material.STONE, 60,
                Material.COAL_ORE, 30,
                Material.COBBLESTONE, 10
    ),
            Arrays.asList(
                            Material.COAL_ORE,
                            Material.DEEPSLATE_COAL_ORE)),

    IRON(Material.IRON_BLOCK,
            "§fIron Tycoon",
            70.0,
            4*20,
            6*20,
            1.0,
            70,
            Map.of(
                Material.STONE, 40,
                Material.IRON_ORE, 40,
                Material.RAW_IRON_BLOCK, 20
    ),
            Arrays.asList(
                            Material.IRON_ORE,
                            Material.DEEPSLATE_IRON_ORE)),

    DIAMOND(Material.DIAMOND_BLOCK,
            "§bDiamond Tycoon",
            100.0,
            3*20,
            5*20,
            1.0,
            100,
            Map.of(
                    Material.DIAMOND_ORE, 50,
                    Material.DEEPSLATE_DIAMOND_ORE, 40,
                    Material.DIAMOND_BLOCK, 10
    ),
            Arrays.asList(
                            Material.DIAMOND_ORE,
                            Material.DEEPSLATE_DIAMOND_ORE)),
    ;

    private final Material material;
    private final String name;
    private final double basePrice;
    private final int spawnInterval;
    private final int miningInterval;
    private final double sellMultiplier;
    private final int defaultMaxInventoryStorage;
    private final Map<Material, Integer> resources;
    private final List<Material> buffMaterials;

    TycoonType(Material material, String name, double basePrice, int spawnInterval, int miningInterval, double sellMultiplier, int defaultMaxInventoryStorage, Map<Material, Integer> resources, List<Material> buffMaterials) {
        this.material = material;
        this.name = name;
        this.basePrice = basePrice;
        this.spawnInterval = spawnInterval;
        this.miningInterval = miningInterval;
        this.sellMultiplier = sellMultiplier;
        this.defaultMaxInventoryStorage = defaultMaxInventoryStorage;
        this.resources = resources;
        this.buffMaterials = buffMaterials;
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
    public double getBasePrice() {
        return basePrice;
    }
    public int getMiningInterval() {
        return miningInterval;
    }
    public double getSellMultiplier() {
        return sellMultiplier;
    }
    public int getDefaultMaxInventoryStorage() {
        return defaultMaxInventoryStorage;
    }
    public Map<Material, Integer> getResources() {
        return resources;
    }
    public List<Material> getBuffMaterials() {
        return buffMaterials;
    }
}
