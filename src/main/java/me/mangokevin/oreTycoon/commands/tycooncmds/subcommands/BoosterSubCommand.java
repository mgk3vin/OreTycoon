package me.mangokevin.oreTycoon.commands.tycooncmds.subcommands;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.tycoonManagment.booster.AutoMinerSpeedBooster;
import me.mangokevin.oreTycoon.tycoonManagment.booster.SellMultiplyBooster;
import me.mangokevin.oreTycoon.tycoonManagment.booster.SpawnSpeedBooster;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

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
        switch (boosterType) {
            case "sellmultiplier":
                player.getInventory().addItem(new SellMultiplyBooster(0.3, 20L * 60 * 2).getItem());
                break;
            case "autominer":
                player.getInventory().addItem(new AutoMinerSpeedBooster(20D, 20L * 60 * 2).getItem());
                break;
            case "spawnspeed":
                player.getInventory().addItem(new SpawnSpeedBooster(20D, 20L * 60 * 2).getItem());
                break;
            case "all":
                player.getInventory().addItem(new SellMultiplyBooster(0.3, 20L * 60 * 2).getItem());
                player.getInventory().addItem(new AutoMinerSpeedBooster(20D, 20L * 60 * 2).getItem());
                player.getInventory().addItem(new SpawnSpeedBooster(20D, 20L * 60 * 2).getItem());
            default:
                player.sendMessage(ChatColor.RED + "Invalid booster type.");
        }


    }
}
