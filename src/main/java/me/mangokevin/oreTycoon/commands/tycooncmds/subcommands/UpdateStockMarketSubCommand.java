package me.mangokevin.oreTycoon.commands.tycooncmds.subcommands;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.worth.WorthManager;
import org.bukkit.entity.Player;

import java.util.List;

public class UpdateStockMarketSubCommand implements TycoonSubCommand{
    private final WorthManager worthManager;
    public UpdateStockMarketSubCommand(OreTycoon plugin){
        this.worthManager = plugin.getWorthManager();
    }
    @Override
    public void execute(Player player, String[] args) {
        worthManager.updateStockMarket();
    }

}
