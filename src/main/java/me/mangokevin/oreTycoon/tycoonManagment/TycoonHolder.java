package me.mangokevin.oreTycoon.tycoonManagment;

import me.mangokevin.oreTycoon.commands.tycooncmds.menuManager.MenuInterface;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TycoonHolder implements InventoryHolder {
    private final MenuInterface menu;

    public TycoonHolder(MenuInterface menu) {
        this.menu = menu;
    }

    public MenuInterface getMenu() {
        return menu;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
