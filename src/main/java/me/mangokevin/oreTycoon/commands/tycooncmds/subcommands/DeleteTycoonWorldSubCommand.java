package me.mangokevin.oreTycoon.commands.tycooncmds.subcommands;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.menuManager.worldMenus.WorldDeleteConfirmMenu;
import me.mangokevin.oreTycoon.tycoonManagment.tycoonWorlds.TycoonWorldManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class DeleteTycoonWorldSubCommand implements TycoonSubCommand {
    private final OreTycoon plugin;
    private final TycoonWorldManager worldManager;

    public DeleteTycoonWorldSubCommand(OreTycoon plugin) {
        this.plugin = plugin;
        this.worldManager = plugin.getTycoonWorldManager();
    }
    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /tycoon " + args[0] + " <world_name>");
            return;
        }
        try {
            String worldName = args[1];

            if (worldManager.getWorldSettings(worldName) != null) {
                new WorldDeleteConfirmMenu(plugin, worldName).open(player);
            } else {
                player.sendMessage(ChatColor.RED + "No world found named: " + args[1]);
            }
        }catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Invalid world name");
        }
    }
    @Override
    public List<String> getTabCompletions(String[] args, Player player) {
        if (args.length == 2) {
            List<String> worldNames = worldManager.getWorldsFromPlayer(player.getUniqueId());
            if (worldNames.isEmpty()) {
                return List.of("No world found!");
            } else {
                return worldNames;
            }
        }
        return List.of();
    }
}
