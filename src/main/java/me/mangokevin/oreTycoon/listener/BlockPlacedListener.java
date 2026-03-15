package me.mangokevin.oreTycoon.listener;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.tycoonManagment.*;
import me.mangokevin.oreTycoon.tycoonManagment.tycoonBlockManagement.TycoonManager;
import me.mangokevin.oreTycoon.tycoonManagment.tycoonBlockManagement.TycoonRegistry;
import me.mangokevin.oreTycoon.utility.Console;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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
    private final TycoonManager tycoonManager;
    private final TycoonRegistry tycoonRegistry;


    public BlockPlacedListener(OreTycoon oreTycoon) {
        this.plugin = oreTycoon;
        this.tycoonManager = plugin.getTycoonManager();
        this.tycoonRegistry = plugin.getTycoonRegistry();
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack itemInHand = event.getItemInHand();
        ItemMeta meta = itemInHand.getItemMeta();
        Block block = event.getBlock();
        Player player = event.getPlayer();

        if (tycoonRegistry.isTycoonBlock(itemInHand)) {
            //Tycoon block is placed
            Console.debug(getClass(), "Item in hand is tycoon block");
            if (tycoonRegistry.getTycoonAmountFromPlayer(player.getUniqueId()) >= tycoonManager.getMaxTycoonsPerPlayer()){
                //Tycoon block limit is already reached
                player.sendMessage(ChatColor.RED + "Total amount of tycoons per player: " + tycoonManager.getMaxTycoonsPerPlayer());
                event.setCancelled(true);
                return;
            }
            //Tycoon block limit is not reached:

            if (tycoonManager.isObstructed(block.getLocation(), player)){
                //Tycoon block location is obstructed
                event.setCancelled(true);
                return;
            }

            //Tycoon Block place Logic
            if (meta != null) {
                PersistentDataContainer pdc = meta.getPersistentDataContainer();
                TycoonBlock tycoonBlock = TycoonData.readFromItem(pdc, player, block, plugin);

                //Register Tycoon
                tycoonRegistry.addTycoon(tycoonBlock);
                tycoonBlock.createHologram();

            }

            block.getWorld().playSound(block.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, 1.0f, 1.5f);
        }

        Location checkLocation = block.getLocation().clone();
        checkLocation.add(0, 1, 0);
        if (tycoonRegistry.isTycoonBlock(checkLocation)) {
            TycoonBlock tycoonBlock = tycoonRegistry.getTycoonBlock(checkLocation);
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
