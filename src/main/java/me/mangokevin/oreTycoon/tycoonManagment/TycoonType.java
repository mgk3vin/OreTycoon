package me.mangokevin.oreTycoon.tycoonManagment;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public enum TycoonType {
    COAL(Material.COAL_BLOCK, "§8Kohle-Tycoon", 100, Map.of(
            Material.STONE, 60, Material.COAL_ORE, 30, Material.COBBLESTONE, 10
    )),
    IRON(Material.IRON_BLOCK, "§fEisen-Tycoon", 80, Map.of(
            Material.STONE, 40, Material.IRON_ORE, 40, Material.RAW_IRON_BLOCK, 20
    )),
    DIAMOND(Material.DIAMOND_BLOCK, "§bDiamant-Tycoon", 60, Map.of(
            Material.DIAMOND_ORE, 50, Material.DEEPSLATE_DIAMOND_ORE, 40, Material.DIAMOND_BLOCK, 10
    ));

    private final Material material;
    private final String name;
    private final int spawnInterval;
    private final Map<Material, Integer> resources;

    TycoonType(Material material, String name, int spawnInterval, Map<Material, Integer> resources) {
        this.material = material;
        this.name = name;
        this.spawnInterval = spawnInterval;
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
    public Map<Material, Integer> getResources() {
        return resources;
    }
}
