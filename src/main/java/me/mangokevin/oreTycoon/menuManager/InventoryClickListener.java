package me.mangokevin.oreTycoon.menuManager;

import me.mangokevin.oreTycoon.tycoonManagment.TycoonHolder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // 1. Ist es ein Inventar mit unserem Holder?
        if (!(event.getInventory().getHolder() instanceof TycoonHolder holder)) return;


        if (event.getCurrentItem() == null) return;
        event.setCancelled(true);

        MenuInterface menu = holder.getMenu();
        if (menu != null) {
            menu.handleAction(event);
        }
    }
}
