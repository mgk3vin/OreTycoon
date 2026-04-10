package me.mangokevin.oreTycoon.commands.tycooncmds.subcommands;

import me.mangokevin.oreTycoon.menuManager.TycoonListMenu;
import org.bukkit.entity.Player;

import java.util.List;

public class TycoonListTypes implements TycoonSubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (args.length >= 1){
            String action = args[0];
            if (action.equalsIgnoreCase("list")){
                new TycoonListMenu().open(player);
            }
        }
    }
    public List<String> getTabCompletions(String[] args, Player player) {
        if (args.length == 1) {
            return List.of("list");
        }
        return List.of();
    }
}
