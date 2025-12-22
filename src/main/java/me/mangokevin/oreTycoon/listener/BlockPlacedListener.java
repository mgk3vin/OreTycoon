package me.mangokevin.oreTycoon.listener;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlockManager;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonData;
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
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class BlockPlacedListener implements Listener {

    private final OreTycoon oreTycoon;
    private final TycoonBlockManager blockManager;
    private final TycoonData tycoonData;

    public BlockPlacedListener(OreTycoon oreTycoon, TycoonBlockManager blockManager, TycoonData tycoonData) {
        this.oreTycoon = oreTycoon;
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


            int level = pdc.getOrDefault(tycoonData.getLEVEL_KEY() ,PersistentDataType.INTEGER, 1);
            int xp = pdc.getOrDefault(tycoonData.getXP_KEY(), PersistentDataType.INTEGER, 0);
            int spawnInterval = pdc.getOrDefault(tycoonData.getSPAWN_INTERVAL_KEY(), PersistentDataType.INTEGER, 5);
            long creationTime = System.currentTimeMillis();
            System.out.println("[BlockPlacedListener] Loading: " + level + "|" + xp + "|" + spawnInterval + "|" + creationTime);

            Material type = event.getBlock().getType();
            Location location = block.getLocation();
            UUID uuid = player.getUniqueId();

            TycoonBlock tycoonBlock = new TycoonBlock(location, uuid, type, false, spawnInterval, oreTycoon, blockManager, blockManager.getLevelManager());

            tycoonBlock.setLevel(level);
            tycoonBlock.setLevelXp(xp);
            tycoonBlock.setCreationTime(creationTime);

            blockManager.addTycoonBlock(tycoonBlock);

            blockManager.getTycoonBlock(block).createHologram();
            block.getWorld().playSound(block.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, 1.0f, 1.5f);

        }

    }
}
