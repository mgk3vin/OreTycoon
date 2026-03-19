package me.mangokevin.oreTycoon.commands.tycooncmds.subcommands;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonType;
import me.mangokevin.oreTycoon.tycoonManagment.tycoonBlockManagement.TycoonManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class GiveTycoonSubCommand implements TycoonSubCommand {
    private final TycoonManager tycoonManager;

    public GiveTycoonSubCommand(OreTycoon plugin) {
        this.tycoonManager = plugin.getTycoonManager();
    }
    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /tycoon " + args[0] + " <type>");
            return;
        }
        String type = args[1];
        if (type == null) {
            return;
        }
        switch (type) {
            case "wood" -> {
                tycoonManager.giveDefaultTycoonBlock(player, TycoonType.WOOD);
            }
            case "jungle" -> {
                tycoonManager.giveDefaultTycoonBlock(player, TycoonType.JUNGLE);
            }
            case "stone" -> {
                tycoonManager.giveDefaultTycoonBlock(player, TycoonType.STONE);
            }
            case "deepslate" -> {
                tycoonManager.giveDefaultTycoonBlock(player, TycoonType.DEEPSLATE);
            }
            case "coal" -> {
                tycoonManager.giveDefaultTycoonBlock(player, TycoonType.COAL);
            }
            case "ocean" -> {
                tycoonManager.giveDefaultTycoonBlock(player, TycoonType.OCEAN);
            }
            case "ice" -> {
                tycoonManager.giveDefaultTycoonBlock(player, TycoonType.ICE);
            }
            case "mesa" -> {
                tycoonManager.giveDefaultTycoonBlock(player, TycoonType.MESA);
            }
            case "wool" -> {
                tycoonManager.giveDefaultTycoonBlock(player, TycoonType.WOOL);
            }
            case "concrete" -> {
                tycoonManager.giveDefaultTycoonBlock(player, TycoonType.CONCRETE);
            }
            case "nether" -> {
                tycoonManager.giveDefaultTycoonBlock(player, TycoonType.NETHER);
            }
            case "iron" -> {
                tycoonManager.giveDefaultTycoonBlock(player,  TycoonType.IRON);
            }
            case "diamond" -> {
                tycoonManager.giveDefaultTycoonBlock(player, TycoonType.DIAMOND);
            }
            case "end" -> {
                tycoonManager.giveDefaultTycoonBlock(player, TycoonType.END);
            }
            default -> {
                player.sendMessage(ChatColor.RED + "Not a valid tycoon type!");
            }
        }
    }
}
