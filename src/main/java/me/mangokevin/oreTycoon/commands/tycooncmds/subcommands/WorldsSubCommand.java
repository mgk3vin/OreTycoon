package me.mangokevin.oreTycoon.commands.tycooncmds.subcommands;


import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.menuManager.worldMenus.WorldSettingsMenu;
import me.mangokevin.oreTycoon.menuManager.worldMenus.WorldsMenu;
import me.mangokevin.oreTycoon.tycoonManagment.tycoonWorlds.TycoonWorldManager;
import org.bukkit.entity.Player;

import java.util.List;

public class WorldsSubCommand implements TycoonSubCommand {

    private final OreTycoon plugin;
    private final TycoonWorldManager tycoonWorldManager;

    public WorldsSubCommand(OreTycoon plugin) {
        this.plugin = plugin;
        this.tycoonWorldManager = plugin.getTycoonWorldManager();
    }

    @Override
    public void execute(Player player, String[] args) {
        String worldName = player.getWorld().getName();
        plugin.getMultiverseCoreApi().getWorldManager().getWorld(worldName)
                .peek(world -> {
                    List<String> worldsOfThisPlayer = tycoonWorldManager.getPlayerWorlds().getOrDefault(player.getUniqueId(), List.of());
                    if (worldsOfThisPlayer.contains(worldName)) {
                        //Owner of this world
                        new WorldSettingsMenu(worldName).open(player);
                    } else {
                        new WorldsMenu(plugin).open(player);
                    }
                })
                .onEmpty(()->{
                    new WorldsMenu(plugin).open(player);
                });
    }
}
