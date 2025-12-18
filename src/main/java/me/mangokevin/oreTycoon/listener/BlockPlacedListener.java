package me.mangokevin.oreTycoon.listener;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlockManager;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BlockPlacedListener implements Listener {

    private final OreTycoon oreTycoon;
    private final TycoonBlockManager blockManager;

    public BlockPlacedListener(OreTycoon oreTycoon, TycoonBlockManager blockManager) {
        this.oreTycoon = oreTycoon;
        this.blockManager = blockManager;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack placedItem = event.getItemInHand();
        ItemMeta meta = placedItem.getItemMeta();
        Block block = event.getBlock();
        Player player = event.getPlayer();

        if (blockManager.isTycoonBlock(placedItem)) {
            //Tycoon block wird platziert
            if (blockManager.getTycoonCount(player.getUniqueId()) >= blockManager.getMaxBlocksPerPlayer()){
                //Tycoon block fällt unter das maximale Limit
                player.sendMessage(ChatColor.RED + "Total amount of tycoons per player: " + blockManager.getMaxBlocksPerPlayer());
                event.setCancelled(true);
                return;
            }
            //Tycoon block fällt noch nicht unter das maximale Limit
            if (blockManager.isObstructed(block.getLocation(), player)){
                //Tycoon block ist nicht weit genug entfernt von einem anderen Tycoon block
                event.setCancelled(true);
                return;
            }
            //event.getPlayer().sendMessage("Du hast einen Tycoon Block platziert!");
            blockManager.addTycoonBlock(block, event.getPlayer().getUniqueId());
            blockManager.getTycoonBlock(block).createHologram();
            block.getWorld().playSound(block.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, 1.0f, 1.5f);


        }

    }
}
