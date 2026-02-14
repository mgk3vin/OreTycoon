package me.mangokevin.oreTycoon.listener.tycoonListener;

import me.mangokevin.oreTycoon.menuManager.*;
import me.mangokevin.oreTycoon.events.tycoonEvents.TycoonAutoMinedEvent;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;

public class TycoonAutoMineListener implements Listener {

    @EventHandler
    public void onTycoonAutoMine(TycoonAutoMinedEvent event) {
        TycoonBlock tycoon = event.getTycoonBlock();
        Inventory inventory = tycoon.getInventory();
        Player owner = tycoon.getOfflineOwner().getPlayer();
        //add Item when Block is auto mined
        boolean itemFits = tycoon.getTycoonInventory().addItem(event.getItemStack());
        if (itemFits) {
            for (org.bukkit.entity.HumanEntity entity : inventory.getViewers()) {
                if (entity instanceof Player player) {
                    tycoon.getTycoonInventory().refresh(player, inventory);
                }
            }
        }else {
            if (owner != null) {
                owner.sendMessage(ChatColor.RED + "Inventory full! Max size: " + tycoon.getInventoryStorage() + "-items");
            }
            tycoon.setAutoMinerEnabled(false);
        }
        //Refresh Tycoon Hologram Worth
        tycoon.updateHologramPreset(tycoon.getLocation(), "WORTH");

        for (Player player : Bukkit.getOnlinePlayers()) {
            Inventory openInventory = player.getOpenInventory().getTopInventory();

            MenuManager.refreshOpenInventory(player, tycoon);

        }


    }
}
