package me.mangokevin.oreTycoon.tycoonManagment.spawnBlocks;

import org.bukkit.Material;

public class SpawnMaterial {

    private final Material material;
    private final int weight;
    private final SpawnMaterialRarity rarity;
    private boolean isActive = true;

    public SpawnMaterial(Material material, int weight, SpawnMaterialRarity rarity) {
        this.material = material;
        this.weight = weight;
        this.rarity = rarity;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Material getMaterial() {
        return material;
    }
    public int getWeight() {
        return weight;
    }
    public SpawnMaterialRarity getRarity() {
        return rarity;
    }
    public boolean isActive() {
        return isActive;
    }
}
