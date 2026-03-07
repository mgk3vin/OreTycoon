package me.mangokevin.oreTycoon.listener;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.menuManager.MenuManager;
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

    private final TycoonManager tycoonManager;
    private final TycoonRegistry tycoonRegistry;
    private final MenuManager menuManager;

    public BlockInteractListener(OreTycoon plugin) {
        this.tycoonManager = plugin.getTycoonManager();
        this.menuManager = plugin.getMenuManager();
        this.tycoonRegistry = plugin.getTycoonRegistry();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block interactedBlock = event.getClickedBlock();

        if (interactedBlock == null) return;

        TycoonBlock tycoonBlock = tycoonRegistry.getTycoonBlock(interactedBlock);

        if (tycoonBlock == null) return;

        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {

            event.setCancelled(true);
            if (!tycoonBlock.isActive()) {
                player.sendMessage(ChatColor.GREEN + "Spawning...");
                tycoonBlock.setActive(true);
            }else{
                player.sendMessage(ChatColor.RED + "Stopped Spawning...");
                tycoonBlock.setActive(false);
            }


        }
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && player.isSneaking()) {
            if (!player.getUniqueId().equals(tycoonBlock.getOwnerUuid())) {
                player.sendMessage(ChatColor.RED + "You can't pickup " + tycoonBlock.getOwnerName() + "'s Tycoon Block!");
                event.setCancelled(true); // Tycoon will not be picked up
                return;
            }
            //Pickup
            if (tycoonRegistry.isTycoonBlock(interactedBlock)) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.AQUA + "Picking up Tycoon Block");
                tycoonManager.pickUpTycoonBlock(player, tycoonBlock, interactedBlock);
                return;
            }
        }
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK){

            if (event.getHand() == EquipmentSlot.OFF_HAND) return;

            if (tycoonRegistry.isTycoonBlock(interactedBlock)) {
                event.setCancelled(true);
                menuManager.openTycoonStats(tycoonBlock, player);
                System.out.println("Tycoon GUI Opened");
            }
        }
    }
}
