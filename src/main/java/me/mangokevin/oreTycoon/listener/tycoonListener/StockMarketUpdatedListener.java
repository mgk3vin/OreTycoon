package me.mangokevin.oreTycoon.listener.tycoonListener;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.menuManager.MenuManager;
import me.mangokevin.oreTycoon.events.tycoonEvents.StockMarketUpdatedEvent;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import me.mangokevin.oreTycoon.tycoonManagment.tycoonBlockManagement.TycoonRegistry;
import me.mangokevin.oreTycoon.utility.Console;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class StockMarketUpdatedListener implements Listener {


    private final TycoonRegistry tycoonRegistry;

    public StockMarketUpdatedListener(OreTycoon plugin) {
        this.tycoonRegistry = plugin.getTycoonRegistry();
    }

    @EventHandler
    public void onStockMarketUpdated(StockMarketUpdatedEvent event) {
        List<TycoonBlock> tycoonBlocksList = tycoonRegistry.getAllTycoons();

        Console.debug(getClass(), "StockMarket Updated Event called");
        if (tycoonBlocksList.isEmpty()) {
            Console.error(getClass(), "No Tycoon blocks found");
            return;
        };
        for (TycoonBlock tycoonBlock : tycoonBlocksList) {
            tycoonBlock.updateHologram();
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            MenuManager.refreshIfOpen(player);
        }

    }
}
