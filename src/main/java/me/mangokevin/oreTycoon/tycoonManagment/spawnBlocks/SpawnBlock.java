package me.mangokevin.oreTycoon.tycoonManagment.spawnBlocks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class SpawnBlock {
    private final SpawnMaterial spawnMaterial;
    private final SpawnMaterialRarity spawnMaterialRarity;
    private final Material material;
    private final Location spawnLocation;
    private final Block block;
    private final World world;


    public SpawnBlock(SpawnMaterial spawnMaterial, Location spawnLocation) {
        this.spawnMaterial = spawnMaterial;
        this.material = spawnMaterial.getMaterial();
        this.spawnMaterialRarity = spawnMaterial.getRarity();
        this.spawnLocation = spawnLocation;
        this.block = spawnLocation.getBlock();
        this.world = spawnLocation.getWorld();
    }

    public Material getMaterial() {
        return material;
    }
    public Location getSpawnLocation() {
        return spawnLocation;
    }
    public SpawnMaterial getSpawnMaterial() {
        return spawnMaterial;
    }
    public SpawnMaterialRarity getSpawnMaterialRarity() {
        return spawnMaterialRarity;
    }
    public Block getBlock() {
        return block;
    }
    public World getWorld() {
        return world;
    }
}
