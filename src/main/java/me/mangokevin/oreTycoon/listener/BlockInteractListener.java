package me.mangokevin.oreTycoon.listener;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.commands.tycooncmds.menuManager.MenuManager;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlockManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.metadata.FixedMetadataValue;

public class BlockInteractListener implements Listener {

    private final OreTycoon plugin;
    private final TycoonBlockManager blockManager;
    private final MenuManager menuManager;

    public BlockInteractListener(OreTycoon plugin, TycoonBlockManager blockManager) {
        this.plugin = plugin;
        this.blockManager = blockManager;
        this.menuManager = plugin.getMenuManager();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (block == null) return;

        TycoonBlock blockData = blockManager.getTycoonBlock(block);

        if (blockData == null) return;

        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (blockManager.isTycoonBlock(block)) {
                TycoonBlock b = blockManager.getTycoonBlock(block);
                event.setCancelled(true);
                if (!b.isActive()) {
                    player.sendMessage(ChatColor.GREEN + "Spawning...");
                    b.setActive(true);
                    //blockManager.startGenerator(b, player);
                }else{
                    player.sendMessage(ChatColor.RED + "Stopped Spawning...");
                    b.setActive(false);
                    //blockManager.stopGenerator(b);
                }

            }
        }
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && player.isSneaking()) {
            if (!player.getUniqueId().equals(blockData.getOwnerUuid())) {
                player.sendMessage(ChatColor.RED + "You can't pickup " + blockData.getOwnerName() + "'s Tycoon Block!");
                event.setCancelled(true); // Block wird nicht zerstört
                return;
            }
            //Pickup
            if (blockManager.isTycoonBlock(block)) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.AQUA + "Picking up Tycoon Block");
                blockManager.pickupTycoonBlock(block, player, blockData);
                return;
            }
        }
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK){

            if (event.getHand() == EquipmentSlot.OFF_HAND) return;

            Block b = event.getClickedBlock();
            Player p = event.getPlayer();
            if (blockManager.isTycoonBlock(b)) {
                TycoonBlock tycoonBlock = blockManager.getTycoonBlock(b);
                event.setCancelled(true);
                //blockManager.openTycoonSpecificMenu(p, tycoonBlock);
                //Replaced by:
                menuManager.openTycoonGui(p, tycoonBlock, tycoonBlock.isActive());
                System.out.println("Tycoon GUI Opened");
            }
        }
    }
}
