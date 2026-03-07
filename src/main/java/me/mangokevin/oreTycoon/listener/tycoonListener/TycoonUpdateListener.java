package me.mangokevin.oreTycoon.listener.tycoonListener;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.events.tycoonEvents.TycoonUpdateEvent;
import me.mangokevin.oreTycoon.sqlite.DatabaseManager;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TycoonUpdateListener implements Listener {

    private final DatabaseManager databaseManager;

    public TycoonUpdateListener(OreTycoon plugin) {
        this.databaseManager = plugin.getDatabaseManager();
    }

    @EventHandler
    public void onTycoonUpdateEvent(TycoonUpdateEvent event) {
        TycoonBlock tycoonBlock = event.getTycoonBlock();

        databaseManager.saveTycoonAsync(tycoonBlock);
    }
}
