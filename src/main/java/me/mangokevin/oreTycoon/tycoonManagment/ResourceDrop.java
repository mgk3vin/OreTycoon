package me.mangokevin.oreTycoon.tycoonManagment;

import org.bukkit.Material;

@Deprecated
public class ResourceDrop {

    private final Material material;
    private final double chance;

    public ResourceDrop(Material material, double chance) {
        this.material = material;
        this.chance = chance;
    }

    // ---------     Getter      ---------
    public Material getMaterial() {
        return material;
    }
    public double getChance() {
        return chance;
    }
    // ---------     Getter      ---------
}
