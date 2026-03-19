package me.mangokevin.oreTycoon.commands.tycooncmds.subcommands;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.menuManager.MenuManager;
import org.bukkit.entity.Player;

public class OpenOverviewMenuSubcommand implements TycoonSubCommand{
    private final MenuManager menuManager;
    public OpenOverviewMenuSubcommand(OreTycoon plugin) {
        this.menuManager = plugin.getMenuManager();
    }
    @Override
    public void execute(Player player, String[] args) {
        menuManager.openTycoonOverview(player, 0);
    }
}
