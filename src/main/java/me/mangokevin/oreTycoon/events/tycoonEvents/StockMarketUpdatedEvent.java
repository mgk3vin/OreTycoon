package me.mangokevin.oreTycoon.events.tycoonEvents;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class StockMarketUpdatedEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();


    public StockMarketUpdatedEvent() {
    }

    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
