package me.mangokevin.oreTycoon.commands.tycooncmds;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.commands.tycooncmds.subcommands.*;
import me.mangokevin.oreTycoon.menuManager.*;
import me.mangokevin.oreTycoon.tycoonManagment.*;
import me.mangokevin.oreTycoon.tycoonManagment.tycoonBlockManagement.TycoonManager;
import me.mangokevin.oreTycoon.tycoonManagment.tycoonBlockManagement.TycoonRegistry;
import me.mangokevin.oreTycoon.tycoonManagment.tycoonWorlds.TycoonWorldManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class TycoonCmd implements CommandExecutor {

    private final OreTycoon plugin;

    private final Map<String, TycoonSubCommand> subCommands = new HashMap<>();

    public TycoonCmd(OreTycoon plugin) {
        this.plugin = plugin;

        register(new WorldsSubCommand(plugin), "worlds", "world", "island");
        register(new CreateTycoonWorldSubCommand(plugin), "create", "new");
        register(new DeleteTycoonWorldSubCommand(plugin), "delete");
        register(new ListWorldsSubCommand(plugin), "list");
        register(new OpenStockMarketMenuSubCommand(), "stockmarket", "market", "worth");
        register(new UpdateStockMarketSubCommand(plugin), "updatestockmarket");
        register(new BoosterSubCommand(plugin), "booster", "boosters");
        register(new RunTestSubCommand(), "runtest", "runtests");
        register(new ToggleAllTycoonsSubcommand(plugin), "toggle_all");
        register(new GiveTycoonSubCommand(plugin), "give", "block");
        register(new OpenOverviewMenuSubcommand(plugin), "menu", "overview", "all");
        register(new OpenStatsMenuSubCommand(plugin), "stats", "info", "open");
    }

    private void register(TycoonSubCommand subCommand, String... aliases) {
        for (String alias : aliases) {
            subCommands.put(alias, subCommand);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!(sender instanceof Player p)) {
            return true;
        }

        if (args.length == 0) {
            new OverviewMenu(plugin, 0).open(p);
            return true;
        }
        String action = args[0].toLowerCase();

        TycoonSubCommand subCommand = subCommands.get(action);
        if (subCommand != null) {
            subCommand.execute(p, args);
        } else {
            p.sendMessage(ChatColor.RED + "Unknown Command!");
        }
        return true;
    }

    public Map<String, TycoonSubCommand> getSubCommands() {
        return subCommands;
    }
}






