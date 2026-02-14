package me.mangokevin.oreTycoon.listener.tycoonListener;

import me.mangokevin.oreTycoon.menuManager.MenuManager;
import me.mangokevin.oreTycoon.events.tycoonEvents.TycoonBoosterTickedEvent;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import me.mangokevin.oreTycoon.tycoonManagment.booster.TycoonBoosterAbstract;
import me.mangokevin.oreTycoon.utility.Console;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TycoonBoosterTickedListener implements Listener {
    @EventHandler
    public void onTycoonTick(TycoonBoosterTickedEvent event) {
        TycoonBlock tycoonBlock = event.getTycoonBlock();
        TycoonBoosterAbstract tycoonBooster = event.getTycoonBooster();
        if (tycoonBooster != null){
            refreshInventory(tycoonBlock);
        }else{
            Console.log(getClass() ,"No booster found");
        }
//        switch (tycoonBooster) {
//            case :
//                refreshInventory(tycoonBlock);
//                break;
//            default:
//                Console.log(getClass() ,"No booster found");
//                break;
//        }
    }
    private void refreshInventory(TycoonBlock tycoonBlock) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            MenuManager.refreshOpenInventory(player, tycoonBlock);
            Console.log(getClass(), "refreshing Booster inventory");
        }
    }
}
