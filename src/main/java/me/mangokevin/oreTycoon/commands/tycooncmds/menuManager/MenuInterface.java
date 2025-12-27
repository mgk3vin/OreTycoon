package me.mangokevin.oreTycoon.commands.tycooncmds.menuManager;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public interface MenuInterface {
    void open(Player player);

    void handleAction(InventoryClickEvent event);
}
