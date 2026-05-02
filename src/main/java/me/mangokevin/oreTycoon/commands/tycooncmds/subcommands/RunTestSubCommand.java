package me.mangokevin.oreTycoon.commands.tycooncmds.subcommands;

import me.mangokevin.oreTycoon.tycoonManagment.upgrades.TycoonUpgrades;
import org.bukkit.entity.Player;

public class RunTestSubCommand implements TycoonSubCommand {
    @Override
    public void execute(Player player, String[] args) {
        if (args.length == 4) {
            try {
                // Umwandeln der Strings in die benötigten Formate
                int level = Integer.parseInt(args[1]);
                double base = Double.parseDouble(args[2]);
                double multi = Double.parseDouble(args[3]);

                // Aufruf deiner Test-Methode
                TycoonUpgrades.testUpgradeCostFunction(level, base, multi);

                player.sendMessage("§aRunning test, view Console!");
            } catch (NumberFormatException e) {
                // Wird ausgelöst, wenn der Spieler keine gültigen Zahlen eingibt
                player.sendMessage("§cERROR");
                player.sendMessage("§7Usage: /command runtest <level, int> <base, double> <multi, double>");
            }
        }
    }
}
