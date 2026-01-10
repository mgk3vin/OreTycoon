package me.mangokevin.oreTycoon.commands.tycooncmds.tycoonListener;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.commands.tycooncmds.menuManager.MenuInterface;
import me.mangokevin.oreTycoon.commands.tycooncmds.menuManager.OverviewMenu;
import me.mangokevin.oreTycoon.commands.tycooncmds.menuManager.StatsMenu;
import me.mangokevin.oreTycoon.commands.tycooncmds.menuManager.TycoonInventory;
import me.mangokevin.oreTycoon.commands.tycooncmds.tycoonEvents.TycoonAutoMinedEvent;
import me.mangokevin.oreTycoon.commands.tycooncmds.utility.Console;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonHolder;
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
            if (openInventory.getHolder() instanceof TycoonHolder tycoonHolder) {
                MenuInterface menu = tycoonHolder.getMenu();
                if (menu == null) {
                    continue;
                };

                switch (menu){
                    case TycoonInventory tycoonInventory -> {
                        if (tycoonInventory.getTycoonBlock().equals(tycoon)) {
                            tycoonInventory.refresh(player, openInventory);
                            Console.log("Refreshing tycoon inventory");
                        }
                    }
                    case StatsMenu statsMenu -> {
                        if (statsMenu.getTycoonBlock().equals(tycoon)) {
                            statsMenu.refresh(player,  openInventory);
                            Console.log("Refreshing stats menu");
                        }
                        //statsMenu.refresh(player, openInventory);
                    }
                    case OverviewMenu overviewMenu -> {
                        if (tycoon.getOfflineOwner().getUniqueId().equals(player.getUniqueId())) {
                            overviewMenu.refresh(player, openInventory);
                            Console.log("Refreshing overview menu");
                        }
                    }
                    default -> {}
                }
            }
        }


    }
}
