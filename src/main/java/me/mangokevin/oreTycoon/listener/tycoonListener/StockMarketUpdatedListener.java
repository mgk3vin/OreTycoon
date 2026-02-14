package me.mangokevin.oreTycoon.listener.tycoonListener;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.menuManager.MenuManager;
import me.mangokevin.oreTycoon.events.tycoonEvents.StockMarketUpdatedEvent;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlockManager;
import me.mangokevin.oreTycoon.utility.Console;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;

public class StockMarketUpdatedListener implements Listener {

    private final OreTycoon plugin;

    public StockMarketUpdatedListener(OreTycoon plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onStockMarketUpdated(StockMarketUpdatedEvent event) {
        TycoonBlockManager tycoonManager = plugin.getBlockManager();
        Map<Location, TycoonBlock> tycoonBlockMap = tycoonManager.getAllTycoonBlocksLocation();
        Console.debug(getClass(), "StockMarket Updated Event called");
        if (tycoonBlockMap.isEmpty()) {
            Console.error(getClass(), "No Tycoon blocks found");
            return;
        };
        for (Map.Entry<Location, TycoonBlock> entry : tycoonBlockMap.entrySet()){

            Location location = entry.getKey();
            TycoonBlock tycoonBlock = entry.getValue();
            tycoonBlock.updateHologramPreset(location, "WORTH");
            Console.debug(getClass(), "Updating Hologram and calling TycoonChangedAttributesEvent");
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            MenuManager.refreshIfOpen(player);
        }

    }
}
