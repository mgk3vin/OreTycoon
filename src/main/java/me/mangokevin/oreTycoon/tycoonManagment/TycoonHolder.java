package me.mangokevin.oreTycoon.tycoonManagment;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TycoonHolder implements InventoryHolder {
    private final TycoonBlock tycoonBlock;
    private final List<TycoonBlock> tycoonBlocksList;
    private final int page;

    public TycoonHolder(TycoonBlock tycoonBlock) {
        this.tycoonBlock = tycoonBlock;
        this.page = 0;
        this.tycoonBlocksList = null;
    }
    public TycoonHolder(List<TycoonBlock> tycoonBlocksList, int page) {
        this.tycoonBlocksList = tycoonBlocksList;
        this.page = page;
        this.tycoonBlock = null;
    }


    public TycoonBlock getTycoonBlock() {
        return tycoonBlock;
    }
    public int getPage() {
        return page;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }
}
