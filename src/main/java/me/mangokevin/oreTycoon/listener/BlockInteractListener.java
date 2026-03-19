package me.mangokevin.oreTycoon.listener;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.menuManager.MenuManager;
import me.mangokevin.oreTycoon.menuManager.StatsMenu;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import me.mangokevin.oreTycoon.tycoonManagment.tycoonBlockManagement.TycoonManager;
import me.mangokevin.oreTycoon.tycoonManagment.tycoonBlockManagement.TycoonRegistry;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class BlockInteractListener implements Listener {

    private final OreTycoon plugin;
    private final TycoonManager tycoonManager;
    private final TycoonRegistry tycoonRegistry;


    public BlockInteractListener(OreTycoon plugin) {
        this.plugin = plugin;
        this.tycoonManager = plugin.getTycoonManager();
        this.tycoonRegistry = plugin.getTycoonRegistry();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block interactedBlock = event.getClickedBlock();
        Action action = event.getAction();

        if (interactedBlock == null) return;

        TycoonBlock tycoonBlock = tycoonRegistry.getTycoonBlock(interactedBlock);

        if (tycoonBlock == null) return;

        switch (action) {
            case LEFT_CLICK_BLOCK -> {
                event.setCancelled(true);
                if (!tycoonBlock.isActive()) {
                    player.sendMessage(ChatColor.GREEN + "Spawning...");
                    tycoonBlock.setActiveByPlayer(true);
                }else{
                    player.sendMessage(ChatColor.RED + "Stopped Spawning...");
                    tycoonBlock.setActiveByPlayer(false);
                }
            }
            case RIGHT_CLICK_BLOCK -> {
                if (player.isSneaking()) {
                    if (!player.getUniqueId().equals(tycoonBlock.getOwnerUuid())) {
                        player.sendMessage(ChatColor.RED + "You can't pickup " + tycoonBlock.getOwnerName() + "'s Tycoon Block!");
                        event.setCancelled(true); // Tycoon will not be picked up
                        return;
                    }
                    //Pickup
                    if (tycoonRegistry.isTycoonBlock(interactedBlock)) {
                        event.setCancelled(true);
                        player.sendMessage(ChatColor.GREEN + "Picking up Tycoon Block");
                        tycoonManager.pickUpTycoonBlock(player, tycoonBlock, interactedBlock);
                        return;
                    }
                }

                if (event.getHand() == EquipmentSlot.OFF_HAND) return;

                if (tycoonRegistry.isTycoonBlock(interactedBlock)) {
                    event.setCancelled(true);
                    new StatsMenu(tycoonBlock, plugin).open(player);
                }
            }
        }
    }
}
