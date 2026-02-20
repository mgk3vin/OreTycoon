package me.mangokevin.oreTycoon.listener;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.scoreboard.ScoreBoardManager;
import me.mangokevin.oreTycoon.worth.WorthManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeaveListener implements Listener {

    private final OreTycoon plugin;
    private final WorthManager worthManager;

    public PlayerLeaveListener(OreTycoon plugin) {
        this.plugin = plugin;
        this.worthManager = plugin.getWorthManager();
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        worthManager.removePlayerFromTimerBar(player);

    }
}
