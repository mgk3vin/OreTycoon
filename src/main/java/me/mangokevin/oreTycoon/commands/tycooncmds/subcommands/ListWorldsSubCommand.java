package me.mangokevin.oreTycoon.commands.tycooncmds.subcommands;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.tycoonManagment.tycoonWorlds.TycoonWorldManager;
import org.bukkit.entity.Player;

public class ListWorldsSubCommand implements TycoonSubCommand {
    private final TycoonWorldManager worldManager;
    public ListWorldsSubCommand(OreTycoon plugin) {
        this.worldManager = plugin.getTycoonWorldManager();
    }
    @Override
    public void execute(Player player, String[] args) {
        worldManager.listTycoonWorlds(player);
    }
}
