package me.mangokevin.oreTycoon.tycoonManagment;

import me.mangokevin.oreTycoon.OreTycoon;
import me.mangokevin.oreTycoon.commands.tycooncmds.utility.Console;
import net.ess3.api.IEssentials;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;

public class PriceUtility {
    public static double calculateWorth(Inventory inventory){
        IEssentials ess = OreTycoon.getEssentials();
        if(ess==null)return 0;

        double worth = 0;
        for(ItemStack item : inventory.getContents()){
            if(item==null || item.getType() == Material.AIR)continue;

            BigDecimal price = ess.getWorth().getPrice(ess, item);

            if(price!=null){
                worth += price.doubleValue() * item.getAmount();
            }
            Console.log("Worth: " + worth + " Price: " + price + " Item: " + item.getType().name());
        }
        return worth;
    }
}
