package me.mangokevin.oreTycoon.tycoonManagment.tycoonBlockManagement;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonData;
import me.mangokevin.oreTycoon.utility.Console;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class TycoonRegistry {
    private final OreTycoon plugin;

    //Maps to save Tycoons
    private final Map<String, TycoonBlock> tycoonUIDs = new HashMap<>();    //Saves UID as key for Tycoon as Value
    private final Map<Location, TycoonBlock> tycoonLocations = new HashMap<>(); //Saves Location as key for Tycoon as Value

    public TycoonRegistry(OreTycoon plugin) {
        this.plugin = plugin;
    }

    public void addTycoon(TycoonBlock tycoonBlock) {
        tycoonUIDs.putIfAbsent(tycoonBlock.getBlockUID(), tycoonBlock);
        tycoonLocations.putIfAbsent(tycoonBlock.getLocation(), tycoonBlock);
        Console.debug(getClass(), "Added tycoon to registry!");
        plugin.getDatabaseManager().saveTycoonAsync(tycoonBlock);
    }
    public void removeTycoon(TycoonBlock tycoonBlock) {
        tycoonUIDs.remove(tycoonBlock.getBlockUID());
        tycoonLocations.remove(tycoonBlock.getLocation());
        Console.log(getClass(), "Removed tycoon from registry!");
        plugin.getDatabaseManager().deleteTycoonAsync(tycoonBlock.getBlockUID());
    }

    public List<TycoonBlock> getAllTycoons() {
        List<TycoonBlock> tycoonBlocks = new ArrayList<>(this.tycoonUIDs.values());
        tycoonBlocks.sort(Comparator.comparingLong(TycoonBlock::getCreationTime));
        return tycoonBlocks;
    }
    public List<TycoonBlock> getAllTycoonsFromPlayer(UUID playerUUID) {
        List<TycoonBlock> tycoonBlocks = new ArrayList<>();
        for (TycoonBlock tycoonBlock : getAllTycoons()) {
            if (tycoonBlock.getOwnerUuid().equals(playerUUID)) {
                tycoonBlocks.add(tycoonBlock);
            }
        }
        tycoonBlocks.sort((a, b) -> Long.compare(a.getCreationTime(), b.getCreationTime()));
        return tycoonBlocks;
    }
    public TycoonBlock getTycoonBlockFromIndex(UUID playerUUID, int index) {
        List<TycoonBlock> tycoonBlocks = getAllTycoonsFromPlayer(playerUUID);
        if (index < 0 || index >= tycoonBlocks.size()) {
            return null;
        }
        return tycoonBlocks.get(index); //0 based
    }
    public TycoonBlock getTycoonBlock(Location location) {
        return tycoonLocations.get(location);
    }
    public TycoonBlock getTycoonBlock(String blockID) {
        return tycoonUIDs.get(blockID);
    }
    public TycoonBlock getTycoonBlock(Block block) {
        return tycoonLocations.get(block.getLocation());
    }
    public boolean isTycoonBlock(ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            return meta.getPersistentDataContainer().has(TycoonData.TYCOON_BLOCK_KEY);
        }
        return false;
    }
    public TycoonBlock getTycoonOfSpawnedBlock(Block block) {
        for (TycoonBlock tycoonBlock : getAllTycoons()) {
            if (tycoonBlock.containsBlock(block)) return tycoonBlock;
        }
        return null;
    }
    public boolean isTycoonBlock(Block block){
        Location location = block.getLocation();
        for (TycoonBlock tycoonBlock : getAllTycoons()) {
            if (tycoonBlock.getLocation().equals(location)) {
                return true;
            }
        }
        return false;
    }
    public boolean isTycoonBlock(Location location){
        TycoonBlock tycoonBlock = getTycoonBlock(location);
        return tycoonBlock != null;
    }
    public boolean isTycoonSpawnedBlock(Block block) {
        return block.hasMetadata("tycoon_id");
    }
    public int getTycoonAmountFromPlayer(UUID playerUUID) {
        return getAllTycoonsFromPlayer(playerUUID).size();
    }
}
