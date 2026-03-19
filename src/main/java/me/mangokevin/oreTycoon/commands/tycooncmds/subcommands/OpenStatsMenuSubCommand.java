package me.mangokevin.oreTycoon.commands.tycooncmds.subcommands;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.menuManager.StatsMenu;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import me.mangokevin.oreTycoon.tycoonManagment.tycoonBlockManagement.TycoonRegistry;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class OpenStatsMenuSubCommand implements TycoonSubCommand {
    private final OreTycoon plugin;
    private final TycoonRegistry tycoonRegistry;

    public OpenStatsMenuSubCommand(OreTycoon plugin) {
        this.plugin = plugin;
        this.tycoonRegistry = plugin.getTycoonRegistry();
    }
    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Incorrect arguments. Use /tycoon open <index>");
            return;
        }
        int index;
        if (args[1] == null) {
            player.sendMessage(ChatColor.RED + "Incorrect arguments. Use /tycoon open <index>");
        }
        try {
            index = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage("§cIndex must be an integer!");
            return;
        }
        TycoonBlock tycoonBlock = tycoonRegistry.getTycoonBlockFromIndex(player.getUniqueId(), index - 1);
        if (tycoonBlock == null) {
            player.sendMessage(ChatColor.RED + "No tycoon block found!");
            return;
        }
        new StatsMenu(tycoonBlock, plugin).open(player);
    }
}
