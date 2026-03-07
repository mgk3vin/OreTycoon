package me.mangokevin.oreTycoon.commands.tycooncmds;


import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.tycoonManagment.TycoonBlock;
import me.mangokevin.oreTycoon.tycoonManagment.tycoonBlockManagement.TycoonRegistry;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Deprecated
public class toggle_selected implements CommandExecutor {

    private final TycoonRegistry tycoonRegistry;

    public toggle_selected(OreTycoon plugin) {
        tycoonRegistry = plugin.getTycoonRegistry();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player player)) return true;

        if (!player.hasPermission("viewing_tycoon")) {
            player.sendMessage(ChatColor.RED + "No Tycoon selected.");
            return true;
        }

        String tycoonUID = player.getMetadata("viewing_tycoon").getFirst().asString();
        TycoonBlock tycoonBlock = tycoonRegistry.getTycoonBlock(tycoonUID);

        if (tycoonBlock == null) return true;

        if (s.equalsIgnoreCase("toggle_selected")) {
            tycoonBlock.setActive(!tycoonBlock.isActive());

            // Soundeffekt für Feedback
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
            if (tycoonBlock.isActive()) {
                player.sendMessage(ChatColor.GREEN + "Tycoon spawning...");
            }else{
                player.sendMessage(ChatColor.RED + "Tycoon spawning stopped .");
            }
        }
        return true;
    }
}
