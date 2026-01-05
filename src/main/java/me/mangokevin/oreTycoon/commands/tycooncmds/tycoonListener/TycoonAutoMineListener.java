package me.mangokevin.oreTycoon.commands.tycooncmds.tycoonListener;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.commands.tycooncmds.menuManager.MenuInterface;
import me.mangokevin.oreTycoon.commands.tycooncmds.menuManager.OverviewMenu;
import me.mangokevin.oreTycoon.commands.tycooncmds.menuManager.StatsMenu;
import me.mangokevin.oreTycoon.commands.tycooncmds.menuManager.TycoonInventory;
import me.mangokevin.oreTycoon.commands.tycooncmds.tycoonEvents.TycoonAutoMinedEvent;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonHolder;
import org.bukkit.Bukkit;
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
        System.out.println("[TycoonAutoMineListener] Event triggered");
        //add Item when Block is auto mined
        boolean itemFits = tycoon.getTycoonInventory().addItem(event.getItemStack());
        if (itemFits) {
            for (org.bukkit.entity.HumanEntity entity : inventory.getViewers()) {
                if (entity instanceof Player player) {
                    tycoon.getTycoonInventory().refresh(player, inventory);
                }
            }
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            Inventory openInventory = player.getOpenInventory().getTopInventory();
            System.out.println("[TycoonAutoMineListener] " + player.getDisplayName() + " is being checked for inventory");
            if (openInventory.getHolder() instanceof TycoonHolder tycoonHolder) {
                MenuInterface menu = tycoonHolder.getMenu();
                if (menu == null) {
                    Bukkit.getLogger().warning("[OreTycoon] Menu is NULL!");
                    continue;
                };
                System.out.println("[TycoonAutoMineListener] menu is not null");

                switch (menu){
                    case TycoonInventory tycoonInventory -> {
                        if (tycoonInventory.getTycoonBlock().equals(tycoon)) {
                            tycoonInventory.refresh(player, openInventory);
                        }
                    }
                    case StatsMenu statsMenu -> {
                        if (statsMenu.getTycoonBlock().equals(tycoon)) {
                            statsMenu.open(player);
                            System.out.println("Opening stats menu");
                        }
                        System.out.println("Refreshing stats menu");
                        statsMenu.refresh(player, openInventory);
                    }
                    case OverviewMenu overviewMenu -> {
                        if (tycoon.getOfflineOwner().getUniqueId().equals(player.getUniqueId())) {
                            overviewMenu.refresh(player, openInventory);
                        }
                    }
                    default -> {}
                }
            }else {
                Bukkit.getLogger().warning("[TycoonAutoMineListener] Holder is not instance of TycoonHolder");
            }
        }


    }
}
