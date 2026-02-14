package me.mangokevin.oreTycoon.listener;

import com.sun.tools.javac.Main;
import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.worth.WorthManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerLeaveListener implements Listener {

    private final WorthManager worthManager = OreTycoon.getInstance().getWorthManager();

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        worthManager.removePlayerFromTimerBar(player);
    }
}
