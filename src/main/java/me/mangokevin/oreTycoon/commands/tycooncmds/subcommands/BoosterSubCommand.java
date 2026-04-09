package me.mangokevin.oreTycoon.commands.tycooncmds.subcommands;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.tycoonManagment.booster.AutoMinerSpeedBooster;
import me.mangokevin.oreTycoon.tycoonManagment.booster.SellMultiplyBooster;
import me.mangokevin.oreTycoon.tycoonManagment.booster.SpawnSpeedBooster;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BoosterSubCommand implements TycoonSubCommand {
    public BoosterSubCommand(OreTycoon plugin) {

    }
    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /tycoon " + args[0] + " <booster_type>");
            return;
        }
        String boosterType = args[1];
        if (boosterType == null) {
            player.sendMessage(ChatColor.RED + "Invalid booster type.");
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

        switch (boosterType) {
            case "sellmultiplier" -> {
                player.getInventory().addItem(new SellMultiplyBooster(0.3, 20L * 60 * 2).getItem(amount));
            }
            case "autominer" -> {
                player.getInventory().addItem(new AutoMinerSpeedBooster(20D, 20L * 60 * 2).getItem(amount));
            }
            case "spawnspeed" -> {
                player.getInventory().addItem(new SpawnSpeedBooster(20D, 20L * 60 * 2).getItem(amount));
            }
            case "all" -> {
                player.getInventory().addItem(new SellMultiplyBooster(0.3, 20L * 60 * 2).getItem(amount));
                player.getInventory().addItem(new AutoMinerSpeedBooster(20D, 20L * 60 * 2).getItem(amount));
                player.getInventory().addItem(new SpawnSpeedBooster(20D, 20L * 60 * 2).getItem(amount));
            }
            default -> {
                player.sendMessage(ChatColor.RED + "Invalid booster type.");
            }
        }
    }
    @Override
    public List<String> getTabCompletions(String[] args, Player player) {
        switch (args.length){
            case 2 -> {
                return List.of(
                        "sellmultiplier",
                        "autominer",
                        "spawnspeed"
                );
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
