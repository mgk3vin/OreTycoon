package me.mangokevin.oreTycoon.commands.tycooncmds;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.commands.tycooncmds.menuManager.MenuManager;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlockManager;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TycoonCmd implements CommandExecutor {

    private final OreTycoon oreTycoon;
    private final TycoonBlockManager blockManager;
    private final MenuManager menuManager;

    public TycoonCmd(OreTycoon oreTycoon, TycoonBlockManager blockManager) {
        this.oreTycoon = oreTycoon;
        this.blockManager = blockManager;
        this.menuManager = oreTycoon.getMenuManager();
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
            case "toggle_all":
                List<TycoonBlock> tycoonBlockList = blockManager.getTycoonBlocksFromPlayer(p.getUniqueId());
                String state;
                if (args[1] == null) {
                    p.sendMessage(ChatColor.RED + "Incorrect arguments. Use /tycoon toggle_all <on/off>");
                    return true;
                }
                state = args[1];
                switch (state) {
                    case "on":
                        for (TycoonBlock tycoonBlock : tycoonBlockList) {
                            tycoonBlock.setActive(true);
                        }
                        return true;
                    case "off":
                        for (TycoonBlock tycoonBlock : tycoonBlockList) {
                            tycoonBlock.setActive(false);
                        }
                        return true;
                    default:
                        p.sendMessage(ChatColor.RED + "Incorrect Usage. Use /tycoon toggle_all <on/off>");
                        return true;
                }

            case "block":

                blockManager.giveTycoonBlock(p, Material.GOLD_BLOCK);
                blockManager.giveTycoonBlock(p, Material.IRON_BLOCK);

                break;
            case "give":
                String type = args[1];
                if (type == null) {
                    return true;
                }
                switch (type) {
                    case "wood":
                        blockManager.giveTycoonBlock(p, TycoonType.WOOD);
                        return true;
                    case "coal":
                        blockManager.giveTycoonBlock(p, TycoonType.COAL);
                        return true;
                    case "iron":
                        blockManager.giveTycoonBlock(p, TycoonType.IRON);
                        return true;
                    case "diamond":
                        blockManager.giveTycoonBlock(p, TycoonType.DIAMOND);
                        return true;
                    default:
                        p.sendMessage(ChatColor.RED + "Not a valid tycoon type!");
                        return true;
                }
            case "menu":
                menuManager.openTycoonOverview(p, 0);
                break;
            case "open":
                int index;
                if (args[1] == null) {
                    p.sendMessage(ChatColor.RED + "Incorrect arguments. Use /tycoon open <index>");
                }
                try {
                    index = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    p.sendMessage("§cIndex must be an integer!");
                    return true;
                }
                TycoonBlock tycoonBlock = blockManager.getTycoonBlockFromIndex(p, index);
                if (tycoonBlock == null) {
                    p.sendMessage(ChatColor.RED + "No tycoon block found!");
                    return true;
                }
                System.out.println("[TycoonCMD] opening index: " + index);
                blockManager.openTycoonSpecificMenu(p, tycoonBlock);
//            default:
//                p.sendMessage(ChatColor.RED + "Unknown command!");
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





