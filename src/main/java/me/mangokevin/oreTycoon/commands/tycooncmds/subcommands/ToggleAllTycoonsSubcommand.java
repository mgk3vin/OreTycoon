package me.mangokevin.oreTycoon.commands.tycooncmds.subcommands;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import me.mangokevin.oreTycoon.tycoonManagment.tycoonBlockManagement.TycoonRegistry;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class ToggleAllTycoonsSubcommand implements TycoonSubCommand{
    private final TycoonRegistry tycoonRegistry;

    public ToggleAllTycoonsSubcommand(OreTycoon plugin) {
        this.tycoonRegistry = plugin.getTycoonRegistry();
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Incorrect arguments. Use /tycoon toggle_all <on/off>");
            return;
        }
        List<TycoonBlock> tycoonBlockList = tycoonRegistry.getAllTycoonsFromPlayer(player.getUniqueId());
        String state;
        if (args[1] == null) {
            player.sendMessage(ChatColor.RED + "Incorrect arguments. Use /tycoon toggle_all <on/off>");
            return;
        }
        state = args[1];
        switch (state) {
            case "on":
                for (TycoonBlock tycoonBlock : tycoonBlockList) {
                    tycoonBlock.setActiveByPlayer(true);
                }
                return;
            case "off":
                for (TycoonBlock tycoonBlock : tycoonBlockList) {
                    tycoonBlock.setActiveByPlayer(false);
                }
                return;
            default:
                player.sendMessage(ChatColor.RED + "Incorrect Usage. Use /tycoon toggle_all <on/off>");
        }
    }
}
