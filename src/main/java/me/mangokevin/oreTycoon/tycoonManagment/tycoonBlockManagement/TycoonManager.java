package me.mangokevin.oreTycoon.tycoonManagment.tycoonBlockManagement;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonType;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class TycoonManager {
    private final TycoonRegistry tycoonRegistry;
    private final TycoonBlockFactory tycoonFactory;

    private final int maxTycoonsPerPlayer;
    public TycoonManager(OreTycoon plugin) {
        this.tycoonRegistry = plugin.getTycoonRegistry();
        this.maxTycoonsPerPlayer = plugin.getConfig().getInt("maxBlocksPerPlayer");
        this.tycoonFactory = plugin.getTycoonFactory();

        new BukkitRunnable() {
            @Override
            public void run() {
                for (TycoonBlock tycoon : tycoonRegistry.getAllTycoons()) {
                    tycoon.incrementAndCheck(); // Every Block counts for itself
                }
            }
        }.runTaskTimer(plugin, 0, 1L); // Runs every second (20 ticks)
    }

    public boolean isObstructed(Location location, Player player) {
        World world = location.getWorld();
        int centerX = location.getBlockX();
        int centerZ = location.getBlockZ();
        int centerY = location.getBlockY();

        for (int x = centerX - 4; x <=  centerX + 4; x++) {
            for (int z = centerZ - 4; z <=  centerZ + 4; z++) {
                for (int y = centerY -2; y <=  centerY + 2; y++) {
                    Location checkLocation = new Location(world, x, y, z);
                    if (tycoonRegistry.isTycoonBlock(checkLocation)){
                        player.sendMessage(ChatColor.RED + "Tycoon blocks must be placed atleast 5 blocks away from eachother!");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void pickUpTycoonBlock(Player player, TycoonBlock removedTycoonBlock, Block block) {
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand.getType() == Material.AIR) {
            //Remove Tycoon logic

            tycoonRegistry.removeTycoon(removedTycoonBlock);
            removedTycoonBlock.removeHologram();
            block.setType(Material.AIR);
            //Remove all spawned Blocks of the removed tycoon
            for (Block activeBlock : removedTycoonBlock.getActiveBlocks()) {
                if (activeBlock.getType() != Material.AIR) {
                    activeBlock.setType(Material.AIR);
                    activeBlock.getWorld().playEffect(activeBlock.getLocation(), Effect.SPONGE_DRY,0);
                }
            }
            for (TycoonBlock tycoonBlock : tycoonRegistry.getAllTycoons()) {
                tycoonBlock.updateHologram();
            }
            //Give smart tycoon
            giveSmartTycoonBlock(player, removedTycoonBlock);
        } else {
            player.sendMessage(ChatColor.RED + "You can only pick up a tycoon with an empty hand!");
        }


    }

    public void giveDefaultTycoonBlock(Player player, TycoonType type) {
        ItemStack itemStack = tycoonFactory.createTycoonBlock(player, type);
        player.getInventory().addItem(itemStack);
    }
    public void giveSmartTycoonBlock(Player player, TycoonBlock smartTycoonBlock) {
        ItemStack itemStack = tycoonFactory.createSmartTycoonBlock(smartTycoonBlock);
        player.getInventory().addItem(itemStack);
    }


    public int getMaxTycoonsPerPlayer() {
        return maxTycoonsPerPlayer;
    }
}
