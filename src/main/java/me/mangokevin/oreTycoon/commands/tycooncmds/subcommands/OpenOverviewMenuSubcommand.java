package me.mangokevin.oreTycoon.commands.tycooncmds.subcommands;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.menuManager.MenuManager;
import me.mangokevin.oreTycoon.menuManager.OverviewMenu;
import org.bukkit.entity.Player;

public class OpenOverviewMenuSubcommand implements TycoonSubCommand{
    private final OreTycoon plugin;
    public OpenOverviewMenuSubcommand(OreTycoon plugin) {
        this.plugin = plugin;
    }
    @Override
    public void execute(Player player, String[] args) {
        new OverviewMenu(plugin, 0).open(player);
    }
}
