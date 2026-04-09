package me.mangokevin.oreTycoon.tycoonManagment;

import me.mangokevin.oreTycoon.tycoonManagment.spawnBlocks.SpawnMaterial;
import me.mangokevin.oreTycoon.tycoonManagment.spawnBlocks.SpawnMaterialRarity;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.*;

public enum TycoonType {
    WOOD(
            Material.CRAFTING_TABLE,
            ChatColor.GOLD + "Wood Tycoon",
            10.0,
            5*20,
            7*20,
            1.0,
            30,
            List.of(
                    new SpawnMaterial(Material.ACACIA_LOG, 10, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.BIRCH_LOG, 10, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.CHERRY_LOG, 10, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.JUNGLE_LOG, 10, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.DARK_OAK_LOG, 10, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.OAK_LOG, 10, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.SPRUCE_LOG, 10, SpawnMaterialRarity.COMMON)
                    ),
          Arrays.asList(
                    Material.OAK_LEAVES,
                    Material.JUNGLE_LEAVES
    )),
    JUNGLE(
            Material.BAMBOO_BLOCK,
            ChatColor.DARK_GREEN + "Jungle Tycoon",
            15.0,
            5*20,
            6*20,
            1.0,
            35,
            List.of(
                    new SpawnMaterial(Material.MOSS_BLOCK,           30, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.MOSS_CARPET,          25, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.MUDDY_MANGROVE_ROOTS, 15, SpawnMaterialRarity.UNCOMMON),
                    new SpawnMaterial(Material.MANGROVE_ROOTS,       10, SpawnMaterialRarity.UNCOMMON),
                    new SpawnMaterial(Material.MUD,                  10, SpawnMaterialRarity.UNCOMMON),
                    new SpawnMaterial(Material.ROOTED_DIRT,          20, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.PODZOL,                5, SpawnMaterialRarity.RARE)
            ), List.of(
            Material.BARRIER
    )),
    NETHER(
            Material.NETHERRACK,
            ChatColor.RED + "Nether Tycoon",
            50.0,
            20 * 5,
            20 * 5,
            1.0,
            50,
            List.of(
                    new SpawnMaterial(Material.NETHERRACK,       30, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.NETHER_BRICKS,    20, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.NETHER_QUARTZ_ORE,10, SpawnMaterialRarity.UNCOMMON),
                    new SpawnMaterial(Material.SOUL_SAND,        30, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.SOUL_SOIL,        30, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.BASALT,           20, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.BLACKSTONE,       15, SpawnMaterialRarity.UNCOMMON),
                    new SpawnMaterial(Material.GLOWSTONE,        15, SpawnMaterialRarity.UNCOMMON),
                    new SpawnMaterial(Material.MAGMA_BLOCK,      20, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.NETHER_GOLD_ORE,  10, SpawnMaterialRarity.RARE),
                    new SpawnMaterial(Material.ANCIENT_DEBRIS,    5, SpawnMaterialRarity.LEGENDARY),
                    new SpawnMaterial(Material.GILDED_BLACKSTONE,15, SpawnMaterialRarity.UNCOMMON)
            ),
            List.of(
                    Material.AIR
            )
    ),
    OCEAN(
            Material.TUBE_CORAL_BLOCK,
            ChatColor.AQUA + "Ocean Tycoon",
            30.0,
            20*4,
            20*5,
            1.0,
            45,
            List.of(
                    new SpawnMaterial(Material.PRISMARINE,        30, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.PRISMARINE_BRICKS, 30, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.SEA_LANTERN,       10, SpawnMaterialRarity.RARE),
                    new SpawnMaterial(Material.DARK_PRISMARINE,   10, SpawnMaterialRarity.RARE),
                    new SpawnMaterial(Material.DRIED_KELP_BLOCK,  15, SpawnMaterialRarity.UNCOMMON),
                    new SpawnMaterial(Material.SPONGE,             5, SpawnMaterialRarity.EPIC),
                    new SpawnMaterial(Material.SAND,              30, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.GRAVEL,            20, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.CLAY,              20, SpawnMaterialRarity.COMMON)
            ),
            List.of(Material.BARRIER)
    ),
    ICE(Material.BLUE_ICE,
            ChatColor.AQUA + "Ice Tycoon",
            45.0,
            4*20,
            5*20,
            1.0,
            50,
            List.of(
                    new SpawnMaterial(Material.ICE,        40, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.PACKED_ICE, 30, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.BLUE_ICE,   15, SpawnMaterialRarity.UNCOMMON),
                    new SpawnMaterial(Material.SNOW_BLOCK, 30, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.SPRUCE_LOG, 15, SpawnMaterialRarity.UNCOMMON)
            ),
            List.of(Material.BARRIER)
    ),
    MESA(Material.TERRACOTTA,
            ChatColor.GOLD + "Mesa Tycoon",
            40.0,
            5*20,
            6*20,
            1.0,
            60,
            List.of(
                    new SpawnMaterial(Material.TERRACOTTA,            30, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.RED_TERRACOTTA,        20, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.ORANGE_TERRACOTTA,     20, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.YELLOW_TERRACOTTA,     15, SpawnMaterialRarity.UNCOMMON),
                    new SpawnMaterial(Material.WHITE_TERRACOTTA,      15, SpawnMaterialRarity.UNCOMMON),
                    new SpawnMaterial(Material.BROWN_TERRACOTTA,      15, SpawnMaterialRarity.UNCOMMON),
                    new SpawnMaterial(Material.GRAY_TERRACOTTA,       15, SpawnMaterialRarity.UNCOMMON),
                    new SpawnMaterial(Material.LIGHT_BLUE_TERRACOTTA, 15, SpawnMaterialRarity.UNCOMMON),
                    new SpawnMaterial(Material.BLACK_TERRACOTTA,      15, SpawnMaterialRarity.UNCOMMON),
                    new SpawnMaterial(Material.PINK_TERRACOTTA,       15, SpawnMaterialRarity.UNCOMMON),
                    new SpawnMaterial(Material.PURPLE_TERRACOTTA,     15, SpawnMaterialRarity.UNCOMMON),
                    new SpawnMaterial(Material.CYAN_TERRACOTTA,       15, SpawnMaterialRarity.UNCOMMON),
                    new SpawnMaterial(Material.MAGENTA_TERRACOTTA,    15, SpawnMaterialRarity.UNCOMMON),
                    new SpawnMaterial(Material.LIGHT_GRAY_TERRACOTTA, 15, SpawnMaterialRarity.UNCOMMON),
                    new SpawnMaterial(Material.GREEN_TERRACOTTA,      15, SpawnMaterialRarity.UNCOMMON),
                    new SpawnMaterial(Material.LIME_TERRACOTTA,       15, SpawnMaterialRarity.UNCOMMON),
                    new SpawnMaterial(Material.BLUE_TERRACOTTA,       15, SpawnMaterialRarity.UNCOMMON),
                    new SpawnMaterial(Material.RED_SANDSTONE,         25, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.RED_SAND,              25, SpawnMaterialRarity.COMMON)
            ),
            List.of(Material.BARRIER)
    ),
    CONCRETE(Material.WHITE_CONCRETE,
            ChatColor.WHITE + "Concrete Tycoon",
            35.0,
            4*20,
            5*20,
            1.0,
            50,

            List.of(
                    new SpawnMaterial(Material.WHITE_CONCRETE,      10, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.ORANGE_CONCRETE,     10, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.MAGENTA_CONCRETE,    10, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.LIGHT_BLUE_CONCRETE, 10, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.YELLOW_CONCRETE,     10, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.LIME_CONCRETE,       10, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.PINK_CONCRETE,       10, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.GRAY_CONCRETE,       10, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.LIGHT_GRAY_CONCRETE, 10, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.CYAN_CONCRETE,       10, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.PURPLE_CONCRETE,     10, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.BLUE_CONCRETE,       10, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.BROWN_CONCRETE,      10, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.GREEN_CONCRETE,      10, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.RED_CONCRETE,        10, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.BLACK_CONCRETE,      10, SpawnMaterialRarity.COMMON)
            ),
            List.of(Material.BARRIER)
    ),
    WOOL(Material.WHITE_WOOL,
            ChatColor.YELLOW + "Wool Tycoon", 20.0, 5*20, 7*20, 1.0, 40,
            List.of(
                    new SpawnMaterial(Material.WHITE_WOOL,      10, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.ORANGE_WOOL,     10, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.MAGENTA_WOOL,    10, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.LIGHT_BLUE_WOOL, 10, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.YELLOW_WOOL,     10, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.LIME_WOOL,       10, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.PINK_WOOL,       10, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.GRAY_WOOL,       10, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.LIGHT_GRAY_WOOL, 10, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.CYAN_WOOL,       10, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.PURPLE_WOOL,     10, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.BLUE_WOOL,       10, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.BROWN_WOOL,      10, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.GREEN_WOOL,      10, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.RED_WOOL,        10, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.BLACK_WOOL,      10, SpawnMaterialRarity.COMMON)
            ),
            List.of(Material.BARRIER)
    ),
    STONE(Material.STONE,
            ChatColor.GRAY + "Stone Tycoon",
            20.0,
            5*20,
            6*20,
            1.0,
            40,
            List.of(
                    new SpawnMaterial(Material.STONE,             15, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.COBBLESTONE,       15, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.ANDESITE,          10, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.DIORITE,           10, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.GRANITE,           10, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.COBBLED_DEEPSLATE,  5, SpawnMaterialRarity.RARE),
                    new SpawnMaterial(Material.TUFF,              15, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.DIRT,              20, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.CALCITE,            7, SpawnMaterialRarity.UNCOMMON),
                    new SpawnMaterial(Material.SMOOTH_BASALT,      5, SpawnMaterialRarity.RARE)
            ),
            List.of(
                    Material.FURNACE
            )),
    DEEPSLATE(Material.DEEPSLATE,
            ChatColor.GRAY + "Deepslate Tycoon",
            30.0,
            4*20,
            5*20,
            1.0,
            50,
            List.of(
                    new SpawnMaterial(Material.DEEPSLATE,           30, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.COBBLED_DEEPSLATE,   25, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.SCULK,               15, SpawnMaterialRarity.UNCOMMON),
                    new SpawnMaterial(Material.AMETHYST_BLOCK,      15, SpawnMaterialRarity.UNCOMMON),
                    new SpawnMaterial(Material.BUDDING_AMETHYST,     5, SpawnMaterialRarity.EPIC),
                    new SpawnMaterial(Material.SMALL_AMETHYST_BUD,  20, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.MEDIUM_AMETHYST_BUD, 15, SpawnMaterialRarity.UNCOMMON),
                    new SpawnMaterial(Material.LARGE_AMETHYST_BUD,  10, SpawnMaterialRarity.RARE),
                    new SpawnMaterial(Material.AMETHYST_CLUSTER,     5, SpawnMaterialRarity.EPIC)
            ),
            List.of(
                    Material.BARRIER
            )),
    COAL(Material.COAL_BLOCK,
            "§8Coal Tycoon",
            30.0,
            5*20,
            7*20,
            1.0,
            40,
            List.of(
                    new SpawnMaterial(Material.STONE,       60, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.COAL_ORE,    30, SpawnMaterialRarity.UNCOMMON),
                    new SpawnMaterial(Material.COBBLESTONE, 10, SpawnMaterialRarity.COMMON)
            ),
            Arrays.asList(
                            Material.COAL_ORE,
                            Material.DEEPSLATE_COAL_ORE
            )),

    IRON(Material.IRON_BLOCK,
            "§fIron Tycoon",
            70.0,
            4*20,
            6*20,
            1.0,
            70,
            List.of(
                    new SpawnMaterial(Material.STONE,         40, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.IRON_ORE,      40, SpawnMaterialRarity.UNCOMMON),
                    new SpawnMaterial(Material.RAW_IRON_BLOCK,20, SpawnMaterialRarity.RARE)
            ),
            Arrays.asList(
                            Material.IRON_ORE,
                            Material.DEEPSLATE_IRON_ORE
            )),

    DIAMOND(Material.DIAMOND_BLOCK,
            "§bDiamond Tycoon",
            100.0,
            3*20,
            5*20,
            1.0,
            100,
            List.of(
                    new SpawnMaterial(Material.DIAMOND_ORE,           50, SpawnMaterialRarity.RARE),
                    new SpawnMaterial(Material.DEEPSLATE_DIAMOND_ORE, 40, SpawnMaterialRarity.EPIC),
                    new SpawnMaterial(Material.DIAMOND_BLOCK,         10, SpawnMaterialRarity.LEGENDARY)
            ),
            Arrays.asList(
                            Material.DIAMOND_ORE,
                            Material.DEEPSLATE_DIAMOND_ORE)),
    END(
            Material.END_STONE,
            ChatColor.LIGHT_PURPLE + "End Tycoon",
            120.0,
            20*4,
            20*4,
            1.0,
            60,
            List.of(
                    new SpawnMaterial(Material.END_STONE,        30, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.END_STONE_BRICKS, 25, SpawnMaterialRarity.COMMON),
                    new SpawnMaterial(Material.PURPUR_BLOCK,     15, SpawnMaterialRarity.UNCOMMON),
                    new SpawnMaterial(Material.OBSIDIAN,         10, SpawnMaterialRarity.RARE),
                    new SpawnMaterial(Material.CRYING_OBSIDIAN,   5, SpawnMaterialRarity.EPIC),
                    new SpawnMaterial(Material.PURPUR_PILLAR,    20, SpawnMaterialRarity.COMMON)
            ),
            List.of(Material.BARRIER)
    ),
    ;

    private final Material material;
    private final String name;
    private final double basePrice;
    private final int spawnInterval;
    private final int miningInterval;
    private final double sellMultiplier;
    private final int defaultMaxInventoryStorage;
    //private final Map<Material, Integer> resources;
    private final List<SpawnMaterial> spawnMaterials;
    private final List<Material> buffMaterials;

    TycoonType(Material material, String name, double basePrice, int spawnInterval, int miningInterval, double sellMultiplier, int defaultMaxInventoryStorage, List<SpawnMaterial> spawnMaterials, List<Material> buffMaterials) {
        this.material = material;
        this.name = name;
        this.basePrice = basePrice;
        this.spawnInterval = spawnInterval;
        this.miningInterval = miningInterval;
        this.sellMultiplier = sellMultiplier;
        this.defaultMaxInventoryStorage = defaultMaxInventoryStorage;
        this.spawnMaterials = spawnMaterials;
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
    public List<SpawnMaterial> getSpawnMaterials() {
        return spawnMaterials;
    }
    public List<Material> getBuffMaterials() {
        return buffMaterials;
    }
}
