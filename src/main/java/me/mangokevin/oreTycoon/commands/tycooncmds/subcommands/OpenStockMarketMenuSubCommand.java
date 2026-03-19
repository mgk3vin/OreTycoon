package me.mangokevin.oreTycoon.commands.tycooncmds.subcommands;

import me.mangokevin.oreTycoon.menuManager.StockMarketMenu;
import org.bukkit.entity.Player;

public class OpenStockMarketMenuSubCommand implements TycoonSubCommand{
    @Override
    public void execute(Player player, String[] args) {
        new StockMarketMenu(0).open(player);
    }
}
