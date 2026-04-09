package me.mangokevin.oreTycoon.commands.tycooncmds.subcommands;

import org.bukkit.entity.Player;

import java.util.List;

public interface TycoonSubCommand {

    void execute(Player player, String[] args);

    default List<String> getTabCompletions(String[] args, Player player) {
        return List.of();
    }
}
