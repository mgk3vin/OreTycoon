package me.mangokevin.oreTycoon.events.tycoonEvents;

import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import me.mangokevin.oreTycoon.tycoonManagment.booster.TycoonBoosterAbstract;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class TycoonBoosterTickedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final TycoonBlock tycoonBlock;
    private final TycoonBoosterAbstract tycoonBooster;

    public TycoonBoosterTickedEvent(TycoonBlock tycoonBlock, TycoonBoosterAbstract tycoonBooster) {
        this.tycoonBlock = tycoonBlock;
        this.tycoonBooster = tycoonBooster;
    }

    public TycoonBlock getTycoonBlock() {
        return tycoonBlock;
    }
    public TycoonBoosterAbstract getTycoonBooster() {
        return tycoonBooster;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
