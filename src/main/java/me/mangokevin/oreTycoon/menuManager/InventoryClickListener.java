package me.mangokevin.oreTycoon.menuManager;

import me.mangokevin.oreTycoon.tycoonManagment.TycoonHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Does inventory have TycoonHolder?
        if (!(event.getInventory().getHolder() instanceof TycoonHolder holder)) return;


        if (event.getCurrentItem() == null) return;
        event.setCancelled(true);

        MenuInterface menu = holder.getMenu();
        if (menu != null) {
            menu.handleAction(event);
        }
    }
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof TycoonHolder holder) {
            MenuInterface menu = holder.getMenu();
            if (menu != null) {
                menu.onClose((Player) event.getPlayer());
            }
        }
    }
}
