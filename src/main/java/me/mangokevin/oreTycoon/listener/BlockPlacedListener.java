package me.mangokevin.oreTycoon.listener;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.tycoonManagment.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

public class BlockPlacedListener implements Listener {

    private final OreTycoon plugin;
    private final TycoonBlockManager blockManager;
    private final TycoonData tycoonData;

    public BlockPlacedListener(OreTycoon oreTycoon, TycoonBlockManager blockManager, TycoonData tycoonData) {
        this.plugin = oreTycoon;
        this.blockManager = blockManager;
        this.tycoonData = tycoonData;
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

            //Tycoon Block place Logik

            assert meta != null;
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            TycoonBlock tycoonBlock = TycoonData.readFromItem(pdc, player, event.getBlock(), plugin);
            block.getWorld().playSound(block.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, 1.0f, 1.5f);
        }

        Location checkLocation = block.getLocation().clone();
        checkLocation.add(0, 1, 0);
        if (blockManager.isTycoonBlock(checkLocation)) {
            TycoonBlock tycoonBlock = blockManager.getTycoonBlock(checkLocation);
            if (tycoonBlock != null) {
                if (tycoonBlock.getTycoonType().getBuffMaterials().contains(block.getType())) {
                    tycoonBlock.activateSellMultiplierBuff();
                    player.sendMessage(ChatColor.GREEN + "Sell Multiplier Buff Activated!");
                    player.playSound(player.getLocation(), Sound.BLOCK_LARGE_AMETHYST_BUD_PLACE, 1.0f, 1.5f);
                }
            }
        }
    }
}
