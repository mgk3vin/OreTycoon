package me.mangokevin.oreTycoon.commands.tycooncmds.menuManager;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public interface MenuInterface {
    void open(Player player);

    void refresh(Player player, Inventory inventory);

    void handleAction(InventoryClickEvent event);
}
