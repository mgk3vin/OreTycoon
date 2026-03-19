package me.mangokevin.oreTycoon.commands.tycooncmds;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.commands.tycooncmds.subcommands.*;
import me.mangokevin.oreTycoon.menuManager.*;
import me.mangokevin.oreTycoon.menuManager.worldMenus.WorldSettingsMenu;
import me.mangokevin.oreTycoon.menuManager.worldMenus.WorldsMenu;
import me.mangokevin.oreTycoon.tycoonManagment.*;
import me.mangokevin.oreTycoon.tycoonManagment.booster.AutoMinerSpeedBooster;
import me.mangokevin.oreTycoon.tycoonManagment.booster.SellMultiplyBooster;
import me.mangokevin.oreTycoon.tycoonManagment.booster.SpawnSpeedBooster;
import me.mangokevin.oreTycoon.tycoonManagment.tycoonBlockManagement.TycoonManager;
import me.mangokevin.oreTycoon.tycoonManagment.tycoonBlockManagement.TycoonRegistry;
import me.mangokevin.oreTycoon.tycoonManagment.tycoonWorlds.TycoonWorldManager;
import me.mangokevin.oreTycoon.worth.WorthManager;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TycoonCmd implements CommandExecutor {

    private final OreTycoon plugin;
    private final TycoonManager tycoonManager;
    private final TycoonRegistry tycoonRegistry;
    private final MenuManager menuManager;
    private final TycoonWorldManager tycoonWorldManager;

    private final Map<String, TycoonSubCommand> subCommands = new HashMap<>();

    public TycoonCmd(OreTycoon plugin) {
        this.plugin = plugin;
        this.tycoonManager = plugin.getTycoonManager();
        tycoonRegistry = plugin.getTycoonRegistry();
        this.menuManager = plugin.getMenuManager();
        this.tycoonWorldManager = plugin.getTycoonWorldManager();

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
}






