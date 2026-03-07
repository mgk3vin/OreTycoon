package me.mangokevin.oreTycoon.scoreboard;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.worth.PriceUtility;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class ScoreBoardManager {

    public ScoreBoardManager() {

    }

    public void setupScoreboard(Player player) {
        Economy economy = OreTycoon.getEconomy();
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("serverObjective", Criteria.DUMMY, ChatColor.AQUA + "" + ChatColor.BOLD + "--- Server ---");


        Team nameTeam = scoreboard.registerNewTeam("name");
        nameTeam.addEntry("§2");
        nameTeam.setPrefix(ChatColor.AQUA + "" + ChatColor.BOLD + "Name: ");
        nameTeam.setSuffix(ChatColor.AQUA +  player.getName());
        objective.getScore("§2").setScore(2);

        Team worldTeam = scoreboard.registerNewTeam("world");
        worldTeam.addEntry("§1");
        worldTeam.setPrefix(ChatColor.AQUA + "" + ChatColor.BOLD + "World: ");
        worldTeam.setSuffix(ChatColor.AQUA +  player.getWorld().getName());
        objective.getScore("§1").setScore(1);

        Team balanceTeam = scoreboard.registerNewTeam("balance");
        balanceTeam.addEntry("§0");
        balanceTeam.setPrefix(ChatColor.AQUA + "" + ChatColor.BOLD + "Balance: ");
        String formattedBalance = PriceUtility.formatMoney(economy.getBalance(player));
        balanceTeam.setSuffix(ChatColor.GREEN +  formattedBalance);
        objective.getScore("§0").setScore(0);

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        player.setScoreboard(scoreboard);
    }
    public void updateScoreboard(Player player) {
        Economy economy = OreTycoon.getEconomy();

        Scoreboard scoreboard = player.getScoreboard();
        Objective objective = scoreboard.getObjective("serverObjective");

        if (objective != null) {
            scoreboard.getTeam("name").setSuffix(ChatColor.AQUA + player.getName());
            scoreboard.getTeam("world").setSuffix(ChatColor.AQUA + player.getWorld().getName());
            String formattedBalance = PriceUtility.formatMoney(economy.getBalance(player));
            scoreboard.getTeam("balance").setSuffix(ChatColor.GREEN +  formattedBalance);
        }
    }
}
