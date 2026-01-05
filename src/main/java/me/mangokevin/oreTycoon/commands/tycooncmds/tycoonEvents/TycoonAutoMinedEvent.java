package me.mangokevin.oreTycoon.commands.tycooncmds.tycoonEvents;

import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class TycoonAutoMinedEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final TycoonBlock tycoonBlock;
    private final ItemStack itemStack;

    public TycoonAutoMinedEvent(TycoonBlock tycoonBlock, ItemStack itemStack) {
        this.tycoonBlock = tycoonBlock;
        this.itemStack = itemStack;
    }

    public TycoonBlock getTycoonBlock() {
        return tycoonBlock;
    }
    public ItemStack getItemStack() {
        return itemStack;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
