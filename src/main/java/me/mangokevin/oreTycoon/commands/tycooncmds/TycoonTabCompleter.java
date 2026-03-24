package me.mangokevin.oreTycoon.commands.tycooncmds;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.commands.tycooncmds.subcommands.TycoonSubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TycoonTabCompleter implements TabCompleter {

    private final Map<String, TycoonSubCommand> subCommands;

    public TycoonTabCompleter(OreTycoon plugin) {
        this.subCommands = plugin.getTycoonCmd().getSubCommands();
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        Player player = (Player) commandSender;
        if (strings.length == 1) {
            List<String> completions = new ArrayList<>();
            for (Map.Entry<String, TycoonSubCommand> entry : subCommands.entrySet()) {
                completions.add(entry.getKey());
            }
            return completions.stream().filter(completion -> completion.startsWith(strings[0])).collect(Collectors.toList());
        }

        if (strings.length >= 2) {
            TycoonSubCommand subCommand = subCommands.get(strings[0].toLowerCase());
            if (subCommand != null) {
                return subCommand.getTabCompletions(strings, player)
                        .stream()
                        .filter(completion -> completion.startsWith(strings[strings.length - 1]))
                        .collect(Collectors.toList());
            }
        }
//        if (strings.length == 2 && strings[0].equalsIgnoreCase("toggle_all")) {
//            List<String> subcommands = Arrays.asList("on", "off");
//
//            return subcommands.stream().filter(string -> string.toLowerCase().startsWith(strings[1].toLowerCase()))
//                    .collect(Collectors.toList());
//        }
//        if (strings.length == 2 && strings[0].equalsIgnoreCase("give")) {
//            List<String> subcommands = Arrays.asList(
//                    "wood",
//                    "jungle",
//                    "stone",
//                    "deepslate",
//                    "coal",
//                    "iron",
//                    "nether",
//                    "ocean",
//                    "ice",
//                    "mesa",
//                    "wool",
//                    "concrete",
//                    "diamond",
//                    "end");
//
//            return subcommands.stream().filter(string -> string.toLowerCase().startsWith(strings[1].toLowerCase()))
//                    .collect(Collectors.toList());
//        }
//        if (strings.length == 2 && strings[0].equalsIgnoreCase("booster")) {
//            List<String> subcommands = Arrays.asList("sellmultiplier", "autominer", "all", "spawnspeed");
//
//            return subcommands.stream().filter(string -> string.toLowerCase().startsWith(strings[1].toLowerCase()))
//                    .collect(Collectors.toList());
//        }

        return List.of();
    }
}
