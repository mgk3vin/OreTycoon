package me.mangokevin.oreTycoon.listener;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.tycoonManagment.tycoonBlockManagement.TycoonRegistry;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.util.List;

public class TycoonManipulationListener implements Listener {

    private final TycoonRegistry tycoonRegistry;

    public TycoonManipulationListener(OreTycoon plugin) {
        this.tycoonRegistry = plugin.getTycoonRegistry();
    }

    @EventHandler
    public void onFallingBlockSpawn(EntitySpawnEvent event) {
        if (!(event.getEntity() instanceof FallingBlock)) return ;

        Block block = event.getEntity().getLocation().getBlock();
        if (tycoonRegistry.isTycoonBlock(block) || tycoonRegistry.isTycoonSpawnedBlock(block)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        List<Block> blocks = event.getBlocks();
        for (Block block : blocks) {
            if (tycoonRegistry.isTycoonBlock(block) || tycoonRegistry.isTycoonSpawnedBlock(block)) {
                event.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        List<Block> blocks = event.getBlocks();
        for (Block block : blocks) {
            if (tycoonRegistry.isTycoonBlock(block) || tycoonRegistry.isTycoonSpawnedBlock(block)) {
                event.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onBlockBurnEvent(BlockBurnEvent event) {
        Block block = event.getBlock();
        if (tycoonRegistry.isTycoonBlock(block) || tycoonRegistry.isTycoonSpawnedBlock(block)) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onBlockFadeEvent(BlockFadeEvent event) {
        Block block = event.getBlock();
        if (tycoonRegistry.isTycoonBlock(block) || tycoonRegistry.isTycoonSpawnedBlock(block)) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onBlockFromToEvent(BlockFromToEvent event) {
        Block block = event.getBlock();
        if (tycoonRegistry.isTycoonBlock(block) || tycoonRegistry.isTycoonSpawnedBlock(block)) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onBlockIgniteEvent(BlockIgniteEvent event) {
        Block block = event.getBlock();
        if (tycoonRegistry.isTycoonBlock(block) || tycoonRegistry.isTycoonSpawnedBlock(block)) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onEntityChangeBlockEvent(EntityChangeBlockEvent event) {
        Block block = event.getBlock();
        if (tycoonRegistry.isTycoonBlock(block) || tycoonRegistry.isTycoonSpawnedBlock(block)) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onBlockPhysicsEvent(BlockPhysicsEvent event) {
        Block block = event.getBlock();
        if (tycoonRegistry.isTycoonBlock(block) || tycoonRegistry.isTycoonSpawnedBlock(block)) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onBlockFertilizeEvent(BlockFertilizeEvent event) {
        Block block = event.getBlock();
        if (tycoonRegistry.isTycoonBlock(block) || tycoonRegistry.isTycoonSpawnedBlock(block)) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onBlockExplodeEvent(BlockExplodeEvent event) {
        Block block = event.getBlock();
        if (tycoonRegistry.isTycoonBlock(block) || tycoonRegistry.isTycoonSpawnedBlock(block)) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onBlockGrow(BlockGrowEvent event) {
        Block block = event.getBlock();
        if (tycoonRegistry.isTycoonBlock(block) || tycoonRegistry.isTycoonSpawnedBlock(block)) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        // Entfernt alle Tycoon-Blöcke aus der Liste der Blöcke, die zerstört werden sollen
        event.blockList().removeIf(block ->
                tycoonRegistry.isTycoonBlock(block) || tycoonRegistry.isTycoonSpawnedBlock(block)
        );
    }

}
