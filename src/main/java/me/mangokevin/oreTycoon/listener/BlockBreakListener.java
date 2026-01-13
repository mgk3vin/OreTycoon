package me.mangokevin.oreTycoon.listener;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.levelManagment.LevelManager;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlockManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;


public class BlockBreakListener implements Listener {

    private final OreTycoon oreTycoon;
    private final TycoonBlockManager blockManager;
    private final LevelManager levelManager;

    public BlockBreakListener(OreTycoon oreTycoon, TycoonBlockManager blockManager, LevelManager levelManager) {
        this.oreTycoon = oreTycoon;
        this.blockManager = blockManager;
        this.levelManager = levelManager;
    }

    @EventHandler
    public void onBlockBreak(org.bukkit.event.block.BlockBreakEvent event) {

        Block block = event.getBlock();
        Location blockLocation = block.getLocation();

        TycoonBlock blockData = blockManager.getTycoonBlock(block);

        Player p = event.getPlayer();



        if (blockData != null) {
            // JA! Es ist ein registrierter Tycoon Block.
            //p.sendMessage(ChatColor.AQUA + "Broke Tycoon Block");

//            blockManager.removeTycoonBlock(block);
//            blockData.removeHologram(blockLocation);
            // ! replace with @pickupTycoonBlock in BlockInteractListener
            event.setCancelled(true);
            p.sendMessage(ChatColor.RED + "SHIFT + Right Click to pickup Tycoon Block!");
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1.5F);

            // 3. Logik anhand der Daten ausführen
            if (!p.getUniqueId().equals(blockData.getOwnerUuid())) {
                p.sendMessage("§cDas ist nicht dein Tycoon Block! Besitzer: " + blockData.getOwnerName());
                event.setCancelled(true); // Block wird nicht zerstört
                return;
            }
        }

        TycoonBlock tycoonBlock = blockManager.getTycoonContainsBlock(block);
        if (tycoonBlock != null) {
            if (tycoonBlock.isAutoMinerEnabled()){
                event.setCancelled(true);
                p.sendMessage(ChatColor.RED + "Cant mine blocks while auto miner is enabled!");
                return;
            }
            //Add item to inv when manually mined
            ItemStack item = new ItemStack(block.getBlockData().getMaterial());
            boolean itemFits = tycoonBlock.getTycoonInventory().addItem(item);
            if (itemFits) {
                event.setCancelled(true);
                block.setType(Material.AIR);
                blockLocation.getWorld().playSound(blockLocation, Sound.ENTITY_ITEM_PICKUP, 1, 1.5F);
            }else {
                p.sendMessage(ChatColor.RED + "Inventory full! Max size: " + tycoonBlock.getInventoryStorage() + "-items");
            }
            //Refresh Tycoon Hologram Worth
            tycoonBlock.updateHologramPreset(tycoonBlock.getLocation(), "WORTH");
            tycoonBlock.handleReward(block);
        }

        Location checkLocation = block.getLocation().clone();
        checkLocation.add(0, 1, 0);
        if (blockManager.isTycoonBlock(checkLocation)) {
            TycoonBlock tycoon = blockManager.getTycoonBlock(checkLocation);
            if (tycoon != null) {
                if (tycoon.getTycoonType().getBuffMaterials().contains(block.getType())) {
                    tycoon.deactivateSellMultiplierBuff();
                    p.sendMessage(ChatColor.RED + "Sell Multiplier Buff Deactivated!");
                    p.playSound(p.getLocation(), Sound.BLOCK_LARGE_AMETHYST_BUD_BREAK, 1.0f, 0.8f);
                }
            }
        }
    }

}
