package me.mangokevin.oreTycoon.listener;

import me.mangokevin.oreTycoon.OreTycoon;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.metadata.FixedMetadataValue;

public class InventoryListener implements Listener {

    private final OreTycoon plugin;

    public InventoryListener(OreTycoon plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        System.out.println("[InventoryListener] InventoryCloseEvent");
        Player player = (Player) event.getPlayer();
        Inventory inventory = event.getInventory();
        if (player.hasMetadata("viewing_tycoon")){
            if (event.getView().getTitle().contains("§bTycoon Stats")){
                System.out.println("[InventoryListener] Removing " + player.getDisplayName() + " Metadata");
                player.removeMetadata("viewing_tycoon", plugin);
            }
        }

    }
}
