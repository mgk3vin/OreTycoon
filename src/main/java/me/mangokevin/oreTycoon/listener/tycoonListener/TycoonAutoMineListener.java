package me.mangokevin.oreTycoon.listener.tycoonListener;

import me.mangokevin.oreTycoon.menuManager.*;
import me.mangokevin.oreTycoon.events.tycoonEvents.TycoonAutoMinedEvent;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import me.mangokevin.oreTycoon.tycoonManagment.spawnBlocks.StoredItemKey;
import me.mangokevin.oreTycoon.tycoonManagment.spawnBlocks.SpawnBlock;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TycoonAutoMineListener implements Listener {

    @EventHandler
    public void onTycoonAutoMine(TycoonAutoMinedEvent event) {
        TycoonBlock tycoon = event.getTycoonBlock();
        Player owner = tycoon.getOfflineOwner().getPlayer();
        SpawnBlock spawnBlock = event.getSpawnBlock();
        StoredItemKey item = event.getItem();

        //add Item when Block is auto mined
        int amount = tycoon.applyFortune();

        boolean itemFits = tycoon.addItem(item, amount);
        if (!itemFits) {
            if (owner != null) {
                owner.sendMessage(ChatColor.RED + "Inventory full! Max size: " + tycoon.getInventoryStorage() + "-items");
            }
            tycoon.setAutoMinerEnabled(false);
        } else {
            tycoon.handleReward(spawnBlock, amount);
        }
        //Refresh Tycoon Hologram
        tycoon.updateHologram();

        for (Player player : Bukkit.getOnlinePlayers()) {
            MenuManager.refreshOpenInventory(player, tycoon);
        }
    }
}
