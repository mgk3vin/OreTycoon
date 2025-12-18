package me.mangokevin.oreTycoon.listener;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlockManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;


public class BlockBreakListener implements Listener {

    private final OreTycoon oreTycoon;
    private final TycoonBlockManager blockManager;

    public BlockBreakListener(OreTycoon oreTycoon, TycoonBlockManager blockManager) {
        this.oreTycoon = oreTycoon;
        this.blockManager = blockManager;
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

        if (blockManager.getTycoonContainsBlock(block) != null) {
            TycoonBlock tycoonBlock = blockManager.getTycoonContainsBlock(block);
            //blockManager.handleXpGain(tycoonBlock, 20);
            tycoonBlock.addTotalXp(20);
            p.sendMessage(ChatColor.GOLD + "Recieved 20 Tycoon xp!");
            tycoonBlock.removeBlock(block);
        }


//        if (block.hasMetadata("tycoon_id")) {
//            String blockID = block.getMetadata("tycoon_id").get(0).asString();
//
//            TycoonBlock tycoonBlock = blockManager.getTycoonBlock(blockID);
//            if (tycoonBlock != null) {
//                tycoonBlock.addTotalXp(20);
//                p.sendMessage(ChatColor.GOLD + "Recieved 20 Tycoon xp!");
//            }
//        }

    }

}
