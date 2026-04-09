package me.mangokevin.oreTycoon.commands.tycooncmds.subcommands;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonType;
import me.mangokevin.oreTycoon.tycoonManagment.tycoonBlockManagement.TycoonManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GiveTycoonSubCommand implements TycoonSubCommand {
    private final TycoonManager tycoonManager;

    public GiveTycoonSubCommand(OreTycoon plugin) {
        this.tycoonManager = plugin.getTycoonManager();
    }
    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /tycoon " + args[0] + " <type> <amount>");
            return;
        }
        String type = args[1];
        if (type == null) {
            return;
        }

        int amount;
        if (args.length == 3) {
            try {
                amount = Integer.parseInt(args[2]);
                if (amount < 1 || amount > 64) {
                    player.sendMessage(ChatColor.RED + "Invalid amount! Must be between 1 and 64");
                    return;
                }
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Invalid amount");
                return;
            }
        } else {
            amount = 1;
        }


        switch (type) {
            case "wood" -> {
                tycoonManager.giveDefaultTycoonBlock(player, TycoonType.WOOD, amount);
            }
            case "jungle" -> {
                tycoonManager.giveDefaultTycoonBlock(player, TycoonType.JUNGLE, amount);
            }
            case "stone" -> {
                tycoonManager.giveDefaultTycoonBlock(player, TycoonType.STONE, amount);
            }
            case "deepslate" -> {
                tycoonManager.giveDefaultTycoonBlock(player, TycoonType.DEEPSLATE, amount);
            }
            case "coal" -> {
                tycoonManager.giveDefaultTycoonBlock(player, TycoonType.COAL, amount);
            }
            case "ocean" -> {
                tycoonManager.giveDefaultTycoonBlock(player, TycoonType.OCEAN, amount);
            }
            case "ice" -> {
                tycoonManager.giveDefaultTycoonBlock(player, TycoonType.ICE, amount);
            }
            case "mesa" -> {
                tycoonManager.giveDefaultTycoonBlock(player, TycoonType.MESA, amount);
            }
            case "wool" -> {
                tycoonManager.giveDefaultTycoonBlock(player, TycoonType.WOOL, amount);
            }
            case "concrete" -> {
                tycoonManager.giveDefaultTycoonBlock(player, TycoonType.CONCRETE, amount);
            }
            case "nether" -> {
                tycoonManager.giveDefaultTycoonBlock(player, TycoonType.NETHER, amount);
            }
            case "iron" -> {
                tycoonManager.giveDefaultTycoonBlock(player,  TycoonType.IRON, amount);
            }
            case "diamond" -> {
                tycoonManager.giveDefaultTycoonBlock(player, TycoonType.DIAMOND, amount);
            }
            case "end" -> {
                tycoonManager.giveDefaultTycoonBlock(player, TycoonType.END, amount);
            }
            default -> {
                player.sendMessage(ChatColor.RED + "Not a valid tycoon type!");
            }
        }
    }
    @Override
    public List<String> getTabCompletions(String[] args, Player player) {
        switch (args.length) {
            case 2 -> {
                return List.of(
                        "wood",
                        "jungle",
                        "stone",
                        "deepslate",
                        "coal",
                        "iron",
                        "nether",
                        "ocean",
                        "ice",
                        "mesa",
                        "wool",
                        "concrete",
                        "diamond",
                        "end");
            }
            case 3 -> {
                List<String> amountList = new ArrayList<>();
                for (int i = 1; i < 64; i++) {
                    amountList.add(Integer.toString(i));
                }
                return amountList;
            }
            default -> {
                return List.of();
            }
        }
    }
}
