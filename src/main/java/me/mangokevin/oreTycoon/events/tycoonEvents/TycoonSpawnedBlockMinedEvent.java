package me.mangokevin.oreTycoon.events.tycoonEvents;

import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import me.mangokevin.oreTycoon.tycoonManagment.spawnBlocks.SpawnBlock;
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
    private final SpawnBlock spawnBlock;

    public TycoonSpawnedBlockMinedEvent(TycoonBlock tycoonBlock, BlockBreakEvent blockBreakEvent, Block block, Player player, SpawnBlock spawnBlock) {
        this.tycoonBlock = tycoonBlock;
        this.block = block;
        this.blockBreakEvent = blockBreakEvent;
        this.player = player;
        this.spawnBlock = spawnBlock;
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
    public SpawnBlock getSpawnBlock() {
        return spawnBlock;
    }
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
