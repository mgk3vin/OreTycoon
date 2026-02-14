package me.mangokevin.oreTycoon.listener.tycoonListener;

import me.mangokevin.oreTycoon.events.tycoonEvents.TycoonChangedAttributesEvent;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TycoonChangedAttributesListener implements Listener {

    @EventHandler
    public void onTycoonChangedAttributesEvent(TycoonChangedAttributesEvent event) {
        TycoonBlock tycoonBlock = event.getTycoonBlock();

        tycoonBlock.updateHologramPreset(tycoonBlock.getLocation(), "ALL");
        tycoonBlock.updateHologramPreset(tycoonBlock.getLocation(), "WORTH");
    }
}
