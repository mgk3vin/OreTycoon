package me.mangokevin.oreTycoon.tycoonManagment;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.*;

import static javax.swing.UIManager.put;

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
    ),      Arrays.asList(
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
            Map.of(
                    Material.MOSS_BLOCK, 30,
                    Material.MOSS_CARPET, 25,
                    Material.MUDDY_MANGROVE_ROOTS, 15,
                    Material.MANGROVE_ROOTS, 10,
                    Material.MUD, 10,
                    Material.ROOTED_DIRT, 20,
                    Material.PODZOL, 5
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
    OCEAN(
            Material.TUBE_CORAL_BLOCK,
            ChatColor.AQUA + "Ocean Tycoon",
            30.0,
            20*4,
            20*5,
            1.0,
            45,
            new  HashMap<Material, Integer>() {{
                put(Material.PRISMARINE, 30);
                put(Material.PRISMARINE_BRICKS, 30);
                put(Material.SEA_LANTERN, 10);
                put(Material.DARK_PRISMARINE, 10);
                put(Material.DRIED_KELP_BLOCK, 15);
                put(Material.SPONGE, 5);
                put(Material.SAND, 30);
                put(Material.GRAVEL, 20);
                put(Material.CLAY, 20);
            }},
            List.of(Material.BARRIER)
    ),
    ICE(Material.BLUE_ICE,
            ChatColor.AQUA + "Ice Tycoon",
            45.0,
            4*20,
            5*20,
            1.0,
            50,
            new HashMap<>() {{
                put(Material.ICE, 40);
                put(Material.PACKED_ICE, 30);
                put(Material.BLUE_ICE, 15);
                put(Material.SNOW_BLOCK, 30);
                put(Material.SPRUCE_LOG, 15);
            }},
            List.of(Material.BARRIER)
    ),
    MESA(Material.TERRACOTTA,
            ChatColor.GOLD + "Mesa Tycoon",
            40.0,
            5*20,
            6*20,
            1.0,
            60,
            new HashMap<>() {{
                put(Material.TERRACOTTA, 30);
                put(Material.RED_TERRACOTTA, 20);
                put(Material.ORANGE_TERRACOTTA, 20);
                put(Material.YELLOW_TERRACOTTA, 15);
                put(Material.WHITE_TERRACOTTA, 15);
                put(Material.BROWN_TERRACOTTA, 15);
                put(Material.GRAY_TERRACOTTA, 15);
                put(Material.LIGHT_BLUE_TERRACOTTA, 15);
                put(Material.BLACK_TERRACOTTA, 15);
                put(Material.PINK_TERRACOTTA, 15);
                put(Material.PURPLE_TERRACOTTA, 15);
                put(Material.CYAN_TERRACOTTA, 15);
                put(Material.MAGENTA_TERRACOTTA, 15);
                put(Material.LIGHT_GRAY_TERRACOTTA, 15);
                put(Material.GREEN_TERRACOTTA, 15);
                put(Material.LIME_TERRACOTTA, 15);
                put(Material.BLUE_TERRACOTTA, 15);

                put(Material.RED_SANDSTONE, 25);
                put(Material.RED_SAND, 25);
            }},
            List.of(Material.BARRIER)
    ),
    CONCRETE(Material.WHITE_CONCRETE,
            ChatColor.WHITE + "Concrete Tycoon",
            35.0,
            4*20,
            5*20,
            1.0,
            50,

            new HashMap<>() {{
        put(Material.WHITE_CONCRETE, 10);
        put(Material.ORANGE_CONCRETE, 10);
        put(Material.MAGENTA_CONCRETE, 10);
        put(Material.LIGHT_BLUE_CONCRETE, 10);
        put(Material.YELLOW_CONCRETE, 10);
        put(Material.LIME_CONCRETE, 10);
        put(Material.PINK_CONCRETE, 10);
        put(Material.GRAY_CONCRETE, 10);
        put(Material.LIGHT_GRAY_CONCRETE, 10);
        put(Material.CYAN_CONCRETE, 10);
        put(Material.PURPLE_CONCRETE, 10);
        put(Material.BLUE_CONCRETE, 10);
        put(Material.BROWN_CONCRETE, 10);
        put(Material.GREEN_CONCRETE, 10);
        put(Material.RED_CONCRETE, 10);
        put(Material.BLACK_CONCRETE, 10);
    }},
            List.of(Material.BARRIER)
    ),
    WOOL(Material.WHITE_WOOL,
            ChatColor.YELLOW + "Wool Tycoon", 20.0, 5*20, 7*20, 1.0, 40,
            new HashMap<>() {{
                put(Material.WHITE_WOOL, 10);
                put(Material.ORANGE_WOOL, 10);
                put(Material.MAGENTA_WOOL, 10);
                put(Material.LIGHT_BLUE_WOOL, 10);
                put(Material.YELLOW_WOOL, 10);
                put(Material.LIME_WOOL, 10);
                put(Material.PINK_WOOL, 10);
                put(Material.GRAY_WOOL, 10);
                put(Material.LIGHT_GRAY_WOOL, 10);
                put(Material.CYAN_WOOL, 10);
                put(Material.PURPLE_WOOL, 10);
                put(Material.BLUE_WOOL, 10);
                put(Material.BROWN_WOOL, 10);
                put(Material.GREEN_WOOL, 10);
                put(Material.RED_WOOL, 10);
                put(Material.BLACK_WOOL, 10);
            }},
            List.of(Material.BARRIER)
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
            Map.of(
                    Material.DEEPSLATE, 30,
                    Material.COBBLED_DEEPSLATE, 25,
                    Material.SCULK, 15,
                    Material.AMETHYST_BLOCK, 15,
                    Material.BUDDING_AMETHYST, 5,
                    Material.SMALL_AMETHYST_BUD, 20,
                    Material.MEDIUM_AMETHYST_BUD, 15,
                    Material.LARGE_AMETHYST_BUD, 10,
                    Material.AMETHYST_CLUSTER, 5
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
            Map.of(
                Material.STONE, 60,
                Material.COAL_ORE, 30,
                Material.COBBLESTONE, 10
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
            Map.of(
                Material.STONE, 40,
                Material.IRON_ORE, 40,
                Material.RAW_IRON_BLOCK, 20
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
            Map.of(
                    Material.DIAMOND_ORE, 50,
                    Material.DEEPSLATE_DIAMOND_ORE, 40,
                    Material.DIAMOND_BLOCK, 10
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
            new  HashMap<Material, Integer>() {{
                put(Material.END_STONE, 30);
                put(Material.END_STONE_BRICKS, 25);
                put(Material.PURPUR_BLOCK, 15);
                //put(Material.CHORUS_PLANT, 10); Causing issues with breaking
                put(Material.OBSIDIAN, 10);
                put(Material.CRYING_OBSIDIAN, 5);
                put(Material.PURPUR_PILLAR, 20);
            }},
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
