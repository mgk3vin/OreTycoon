package me.mangokevin.oreTycoon.events.tycoonEvents;

import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

public class TycoonSpawnedBlockMinedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final TycoonBlock tycoonBlock;
    private final Block block;
    private final BlockBreakEvent blockBreakEvent;
    private final Player player;

    public TycoonSpawnedBlockMinedEvent(TycoonBlock tycoonBlock, BlockBreakEvent blockBreakEvent, Block block, Player player) {
        this.tycoonBlock = tycoonBlock;
        this.block = block;
        this.blockBreakEvent = blockBreakEvent;
        this.player = player;
    }

    public TycoonBlock getTycoonBlock() {
        return tycoonBlock;
    }
    public Block getBlock() {
        return block;
    }
    public BlockBreakEvent getBlockBreakEvent() {
        return blockBreakEvent;
    }
    public Player getPlayer() {
        return player;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
