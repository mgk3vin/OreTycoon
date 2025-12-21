package me.mangokevin.oreTycoon.commands.tycooncmds;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlockManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TycoonCmd implements CommandExecutor {

    private final OreTycoon oreTycoon;
    private final TycoonBlockManager blockManager;

    public TycoonCmd(OreTycoon oreTycoon, TycoonBlockManager blockManager) {
        this.oreTycoon = oreTycoon;
        this.blockManager = blockManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!(sender instanceof Player p)) {
            return true;
        }

        if (args.length == 0) {
            //Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "dm open tycoon_menu " + p.getName());
            blockManager.openTycoonMenu(p);
            return true;
        }
        String action = args[0].toLowerCase();

        switch (action) {
            case "toggle_selected":
                handleToggle(p);
                break;
            case "block":
                blockManager.giveTycoonBlock(p, Material.GOLD_BLOCK);
                break;
            case "menu":
//                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "dm open tycoon_menu " + p.getName());
                blockManager.openTycoonMenu(p);
                break;
        }

        return true;
    }


    public void handleToggle(Player player){
        if (!player.hasMetadata("viewing_tycoon")) {
            player.sendMessage(ChatColor.RED + "No Tycoon selected.");
            return;
        }

        String tycoonUID = player.getMetadata("viewing_tycoon").getFirst().asString();
        TycoonBlock tycoonBlock = blockManager.getTycoonBlock(tycoonUID);

        if (tycoonBlock == null) return;


        tycoonBlock.setActive(!tycoonBlock.isActive());

        // Soundeffekt für Feedback
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
        if (tycoonBlock.isActive()) {
            player.sendMessage(ChatColor.GREEN + "Tycoon spawning...");
        }else{
            player.sendMessage(ChatColor.RED + "Tycoon spawning stopped.");
        }
    }
}





