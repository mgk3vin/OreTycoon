package me.mangokevin.oreTycoon.tycoonListener;

import me.mangokevin.oreTycoon.menuManager.MenuManager;
import me.mangokevin.oreTycoon.menuManager.TycoonBoosterMenu;
import me.mangokevin.oreTycoon.tycoonEvents.TycoonBoosterTickedEvent;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import me.mangokevin.oreTycoon.tycoonManagment.booster.TycoonBoosterAbstract;
import me.mangokevin.oreTycoon.utility.Console;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;

public class TycoonBoosterTickedListener implements Listener {
    @EventHandler
    public void onTycoonTick(TycoonBoosterTickedEvent event) {
        TycoonBlock tycoonBlock = event.getTycoonBlock();
        TycoonBoosterAbstract tycoonBooster = event.getTycoonBooster();
        switch (tycoonBooster.getUID().toLowerCase()) {
            case "sell_multiplier_booster", "auto_miner_booster":
                refreshInventory(tycoonBlock);
                break;
            default:
                Console.log(getClass() ,"No booster found");
                break;
        }
    }
    private void refreshInventory(TycoonBlock tycoonBlock) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            MenuManager.refreshOpenInventory(player, tycoonBlock);
            Console.log(getClass(), "refreshing Booster inventory");
        }
    }
}
