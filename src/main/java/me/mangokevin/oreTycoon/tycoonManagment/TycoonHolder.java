package me.mangokevin.oreTycoon.tycoonManagment;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class TycoonHolder implements InventoryHolder {
    private final TycoonBlock tycoonBlock;

    public TycoonHolder(TycoonBlock tycoonBlock) {
        this.tycoonBlock = tycoonBlock;
    }

    public TycoonBlock getTycoonBlock() {
        return tycoonBlock;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }
}
