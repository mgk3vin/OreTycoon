package me.mangokevin.oreTycoon.commands.tycooncmds;

import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlockManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TycoonTabCompleter implements TabCompleter {


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (strings.length == 1) {
            List<String> subcommands = Arrays.asList("toggle_selected", "toggle_all","upgrade", "block","info", "stats", "menu", "open");

            return subcommands.stream().filter(string -> string.toLowerCase().startsWith(strings[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (strings.length == 2 || strings[1].equalsIgnoreCase("toggle_all")) {
            List<String> subcommands = Arrays.asList("on", "off");

            return subcommands.stream().filter(string -> string.toLowerCase().startsWith(strings[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return List.of();
    }
}
