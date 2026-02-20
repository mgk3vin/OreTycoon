package me.mangokevin.oreTycoon.listener;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.scoreboard.ScoreBoardManager;
import me.mangokevin.oreTycoon.worth.WorthManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final OreTycoon plugin;
    private final WorthManager worthManager;
    private final ScoreBoardManager scoreBoardManager;

    public PlayerJoinListener(OreTycoon plugin) {
        this.plugin = plugin;
        this.worthManager = plugin.getWorthManager();
        this.scoreBoardManager = plugin.getScoreboardManager();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        worthManager.addPlayerToTimerBar(player);

        scoreBoardManager.setupScoreboard(event.getPlayer());
    }

}
