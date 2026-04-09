package me.mangokevin.oreTycoon.listener.tycoonListener;

import me.mangokevin.oreTycoon.events.tycoonEvents.TycoonSpawnedBlockMinedEvent;
import me.mangokevin.oreTycoon.menuManager.MenuManager;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonUpgrades;
import me.mangokevin.oreTycoon.tycoonManagment.spawnBlocks.StoredItemKey;
import me.mangokevin.oreTycoon.tycoonManagment.spawnBlocks.SpawnBlock;
import me.mangokevin.oreTycoon.utility.Console;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;


public class TycoonSpawnedBlockMinedListener implements Listener {

    @EventHandler
    public void onTycoonSpawnedBlockMined(TycoonSpawnedBlockMinedEvent event) {
        TycoonBlock tycoonBlock = event.getTycoonBlock();
        Block block = event.getBlock();
        Player player = event.getPlayer();
        BlockBreakEvent blockBreakEvent = event.getBlockBreakEvent();
        SpawnBlock spawnBlock = event.getSpawnBlock();

        if (tycoonBlock == null) {
            Console.error(getClass(),"TYCOON BLOCK IS NULL!");
            return;
        }
        //Stop spawned block mining when autominer of tycoon is enabled
        if (tycoonBlock.isAutoMinerEnabled()){
            blockBreakEvent.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Cant mine blocks while auto miner is enabled!");
            return;
        }

        //Add item to inv when manually mined
        ItemStack item = new ItemStack(block.getType());

        StoredItemKey key = new StoredItemKey(spawnBlock.getMaterial(), spawnBlock.getSpawnMaterialRarity());

        //should Fortune Multiplier activate
        if (TycoonUpgrades.shouldFortuneActivate(tycoonBlock)){
            item.setAmount(2);
        }
        if (tycoonBlock.addItem(key, 1)) {
            MenuManager.refreshOpenInventory(player, tycoonBlock);
            blockBreakEvent.setCancelled(true);

            tycoonBlock.handleReward(spawnBlock);
            block.setType(Material.AIR);
            tycoonBlock.getLocation().getWorld().playSound(tycoonBlock.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1.5F);
        }else {
            player.sendMessage(ChatColor.RED + "Inventory full! Max size: " + tycoonBlock.getStorageStatisticFormatted());
        }
        //Refresh Tycoon Hologram Worth
        tycoonBlock.updateHologram();


    }
}
