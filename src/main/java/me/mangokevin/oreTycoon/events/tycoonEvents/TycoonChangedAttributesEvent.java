package me.mangokevin.oreTycoon.events.tycoonEvents;

import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class TycoonChangedAttributesEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final TycoonBlock tycoonBlock;

    public TycoonChangedAttributesEvent(TycoonBlock tycoonBlock) {
        this.tycoonBlock = tycoonBlock;
    }

    public TycoonBlock getTycoonBlock() {
        return tycoonBlock;
    }


    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}
