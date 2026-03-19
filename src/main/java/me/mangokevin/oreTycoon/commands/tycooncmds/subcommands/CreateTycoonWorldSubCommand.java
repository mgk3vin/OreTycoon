package me.mangokevin.oreTycoon.commands.tycooncmds.subcommands;


import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.tycoonManagment.tycoonWorlds.TycoonWorldManager;
import org.bukkit.entity.Player;

public class CreateTycoonWorldSubCommand implements TycoonSubCommand {
    private final TycoonWorldManager worldManager;
    public CreateTycoonWorldSubCommand(OreTycoon plugin) {
        this.worldManager = plugin.getTycoonWorldManager();
    }
    @Override
    public void execute(Player player, String[] args) {
        worldManager.createTycoonWorld(player);
    }
}
