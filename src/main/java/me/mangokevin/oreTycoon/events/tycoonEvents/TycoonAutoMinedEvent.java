package me.mangokevin.oreTycoon.events.tycoonEvents;

import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import me.mangokevin.oreTycoon.tycoonManagment.spawnBlocks.StoredItemKey;
import me.mangokevin.oreTycoon.tycoonManagment.spawnBlocks.SpawnBlock;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TycoonAutoMinedEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final TycoonBlock tycoonBlock;
    private final SpawnBlock spawnBlock;
    private final StoredItemKey item;

    public TycoonAutoMinedEvent(TycoonBlock tycoonBlock, SpawnBlock spawnBlock, StoredItemKey item) {
        this.tycoonBlock = tycoonBlock;
        this.spawnBlock = spawnBlock;
        this.item = item;
    }

    public TycoonBlock getTycoonBlock() {
        return tycoonBlock;
    }
    public StoredItemKey getItem() {
        return item;
    }
    public SpawnBlock getSpawnBlock() {
        return spawnBlock;
    }
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
