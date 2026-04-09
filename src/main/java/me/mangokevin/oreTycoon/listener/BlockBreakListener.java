package me.mangokevin.oreTycoon.listener;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.events.tycoonEvents.TycoonSpawnedBlockMinedEvent;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import me.mangokevin.oreTycoon.tycoonManagment.spawnBlocks.SpawnBlock;
import me.mangokevin.oreTycoon.tycoonManagment.tycoonBlockManagement.TycoonRegistry;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;


public class BlockBreakListener implements Listener {


    private final TycoonRegistry tycoonRegistry;

    public BlockBreakListener(OreTycoon oreTycoon) {
        this.tycoonRegistry = oreTycoon.getTycoonRegistry();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        Block block = event.getBlock();
        Location blockLocation = block.getLocation();

        TycoonBlock tycoonBlock = tycoonRegistry.getTycoonBlock(blockLocation);

        Player player = event.getPlayer();


        if (tycoonBlock != null) {
            //Broken block is tycoon Block

            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "SHIFT + Right Click to pickup Tycoon Block!");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1.5F);

            //Case when breaking someone else tycoon
            if (!player.getUniqueId().equals(tycoonBlock.getOwnerUuid())) {
                player.sendMessage("§cThis is not your tycoon. Owner: " + tycoonBlock.getOwnerName());
                event.setCancelled(true); // Cancel break event
                return;
            }
        }


        TycoonBlock tycoonBlockInteracted = tycoonRegistry.getTycoonOfSpawnedBlock(block);
        if (tycoonBlockInteracted == null) return;
        SpawnBlock spawnBlock = tycoonBlockInteracted.getSpawnBlockFromBlock(block);
        //call the tycoon spawned block is mined event to handle
        Bukkit.getPluginManager().callEvent(new TycoonSpawnedBlockMinedEvent(tycoonBlockInteracted, event, block, player, spawnBlock));

        Location checkLocation = block.getLocation().clone();
        checkLocation.add(0, 1, 0);
        if (tycoonRegistry.isTycoonBlock(checkLocation)) {
            TycoonBlock tycoon = tycoonRegistry.getTycoonBlock(checkLocation);
            if (tycoon != null) {
                if (tycoon.getTycoonType().getBuffMaterials().contains(block.getType())) {
                    tycoon.deactivateSellMultiplierBuff();
                    player.sendMessage(ChatColor.RED + "Sell Multiplier Buff Deactivated!");
                    player.playSound(player.getLocation(), Sound.BLOCK_LARGE_AMETHYST_BUD_BREAK, 1.0f, 0.8f);
                }
            }
        }
    }

}
